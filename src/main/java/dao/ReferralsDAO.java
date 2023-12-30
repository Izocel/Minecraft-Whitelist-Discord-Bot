package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import services.sentry.SentryService;

public class ReferralsDAO extends BaseDao {
    private Logger logger;
    private String userTable;

    public ReferralsDAO(ComboPooledDataSource poolDs) {
        super(poolDs);
        this.userTable = "wdmc_users";
        this.tableName = "wdmc_referrals";
        this.logger = Logger.getLogger("WDMC:" + this.getClass().getSimpleName());
    }
    
    @Override
    public Integer save(JSONObject sqlProps) {
        int id = sqlProps.optInt("id");
        final String userKey = sqlProps.optString("user_key");
        final String referralKey = sqlProps.optString("referral_key");
        final String invitedBy = sqlProps.optString("invited_by");
        final String data = sqlProps.optString("data");

        try {
            int status = -1;

            // Insert
            if (id < 1) {

                String sql = "INSERT INTO " + this.tableName + " (user_key, referral_key, invited_by, data) " +
                        "VALUES (?,?,?,?);";

                final PreparedStatement preStmt = this.getConnection().prepareStatement(sql, new String[] { "id" });
                preStmt.setString(1, userKey);
                preStmt.setString(2, referralKey);
                preStmt.setObject(3, invitedBy);
                preStmt.setString(4, data);
                status = preStmt.executeUpdate();
                ResultSet generatedKeys = preStmt.getGeneratedKeys();

                while (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                    break;
                }

                preStmt.close();
                this.closeConnection();
            }

            // Update
            else if (this.find(id) != null) {
                String sql = "UPDATE " + this.tableName + " SET " +
                        "data = ?," +
                        "referral_key = ?," +
                        "invited_by = ?," +
                        "updated_at = CURRENT_TIMESTAMP " +
                        "WHERE id = ?;";

                final PreparedStatement preStmt = this.getConnection().prepareStatement(sql);
                preStmt.setString(1, data);
                preStmt.setObject(2, referralKey);
                preStmt.setObject(3, invitedBy);
                preStmt.setInt(4, id);
                status = preStmt.executeUpdate();
                id = preStmt.getUpdateCount() > 0 ? id : -1;
                preStmt.close();
                this.closeConnection();
            }
        } catch (Exception e) {
            SentryService.captureEx(e);
        }
        return id;
    }
}

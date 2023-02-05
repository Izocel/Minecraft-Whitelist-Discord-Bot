package dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import services.sentry.SentryService;

public interface IDao {

    abstract Integer save(JSONObject json);

    default JSONArray toJsonArray(ResultSet resultSet) {

        if (resultSet == null) {
            return null;
        }

        JSONArray results = new JSONArray();

        try {
            ResultSetMetaData md = resultSet.getMetaData();

            int numCols = md.getColumnCount();
            List<String> colNames = IntStream.range(0, numCols)
                    .mapToObj(i -> {
                        try {
                            return md.getColumnName(i + 1);
                        } catch (SQLException e) {
                            SentryService.captureEx(e);
                            return "?";
                        }
                    })
                    .collect(Collectors.toList());

            while (resultSet.next()) {
                JSONObject row = new JSONObject();
                colNames.forEach(cn -> {
                    try {
                        row.put(cn, resultSet.getObject(cn));
                    } catch (JSONException | SQLException e) {
                        SentryService.captureEx(e);
                    }
                });
                results.put(row);
            }

        } catch (SQLException e) {
            SentryService.captureEx(e);
        }

        return results;
    }
    
}
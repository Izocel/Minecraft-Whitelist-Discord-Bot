package models;
import org.json.JSONObject;

import dao.BaseDao;

public interface IModel {
    abstract Integer save(BaseDao dao);
    abstract JSONObject toJson();
    abstract BaseModel deepCopy(BaseModel model);
}

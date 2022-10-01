package models;

import org.apache.commons.lang.NotImplementedException;
import org.json.JSONObject;

import dao.BaseDao;

public class BaseModel extends Object implements IModel {

    private Integer id;

    @Override
    public Integer save(BaseDao dao) {
        return dao.save(this.toJson());
    }

    @Override
    public Integer delete(BaseDao dao) {
        return dao.delete(this);
    }

    @Override
    public JSONObject toJson() {
        throw new NotImplementedException();
    }

    @Override
    public BaseModel deepCopy(BaseModel model) {
        throw new NotImplementedException();
    }

    public int gettId() {
        return id;
    }
    
}

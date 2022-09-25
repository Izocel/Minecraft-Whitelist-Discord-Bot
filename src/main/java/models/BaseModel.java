package models;

import org.apache.commons.lang.NotImplementedException;
import org.json.JSONObject;

import dao.BaseDao;

public class BaseModel extends Object implements IModel {

    @Override
    public Integer save(BaseDao dao) {
        return dao.save(this.toJson());
    }

    @Override
    public JSONObject toJson() {
        throw new NotImplementedException();
    }

    @Override
    public BaseModel deepCopy(BaseModel model) {
        throw new NotImplementedException();
    }
    
}

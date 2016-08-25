package win.doyto.fzone.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * Class AbstractModel ...
 *
 * @author f0rb
 * @version 1.0.0 2011-1-2
 */
public abstract class CommonModel<T extends CommonModel<?>> implements Cloneable, Serializable {
    /** 查询多条记录时存放在list里 */
    private List<T> list;
    //private String domain = getClass().getSimpleName();
    //private String action = "*";
    private Map<String, List<String>> messages;

    /**
     * 获取子类的名称
     * </p>
     * 子Model的命名应与对应的表名保持一致
     *
     * @return 子类的名称
     */
    //public String getDomain() {
    //    return domain;
    //}
    public boolean needPaging() {
        return false;
    }

    public void fillBy(final T t) {
        throw new UnsupportedOperationException();
    }

    /**
     * 返回一个初始化好数据的model对象，用于数据库插入
     *
     * @return T 待插入数据库的model对象
     */
    public T toInsertModel() {
        return clone();
    }

    /**
     * 返回一个装填了内部数据的model对象
     * <p/>
     * 默认返回用初始化数据填充的model
     *
     * @return T 待插入数据库的model对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public T clone() {
        try {
            return (T) super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /*public synchronized void setMessages(Map<String, List<String>> messages) {
        this.messages = messages;
    }*/

    public synchronized Map<String, List<String>> getMessages() {
        return hasMessages() ? new LinkedHashMap<String, List<String>>(internalGetMessages()) : null;
    }

    public synchronized void addMessage(Object message) {
        addMessage(message.toString());
    }

    //public synchronized void addMessage(String message) {
    //    addMessage(action, message);
    //}

    public synchronized void addMessage(String fieldName, Object message) {
        addMessage(fieldName, message.toString());
    }

    public synchronized void addMessage(String fieldName, String message) {
        final Map<String, List<String>> messages = internalGetMessages();
        List<String> filedMessages = messages.get(fieldName);

        if (filedMessages == null) {
            filedMessages = new ArrayList<String>();
            messages.put(fieldName, filedMessages);
        }

        filedMessages.add(message);
    }

    public synchronized boolean hasMessages() {
        return (messages != null) && !messages.isEmpty();
    }

    private synchronized Map<String, List<String>> internalGetMessages() {
        if (messages == null) {
            messages = new LinkedHashMap<String, List<String>>();
        }
        return messages;
    }

    public synchronized void clearMessages() {
        internalGetMessages().clear();
    }

    /**
     * Method getList returns the list of this AbstractModel object.
     *
     * @return the list (type Object[]) of this AbstractModel object.
     */
    public List<T> getList() {
        return list;
    }

    /**
     * Method setList sets the list of this AbstractModel object.
     *
     * @param list the list of this AbstractModel object.
     */
    public void setList(List<T> list) {
        this.list = list;
    }

    //public String getAction() {
    //    return action;
    //}

    //public void setAction(String action) {
    //    this.action = action;
    //}

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

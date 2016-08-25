package win.doyto.fzone.common;

/**
 * Class PageableModel ...
 *
 * @author f0rb
 * @version 1.0.0 2011-1-2
 */
public abstract class PageableModel<T extends PageableModel<?>> extends CommonModel<T>
        implements Pageable<T> {

    private Page page;

    public boolean needPaging() {
        return page != null;// page对象不为空时，需要数据库分页
    }

    private synchronized Page internalGetPage() {
        if (page == null) page = new Page();
        return page;
    }

    /**
     * Method getOffset returns the offset of this AbstractModel object.
     *
     * @return the offset (type Integer) of this AbstractModel object.
     */
    public Long getOffset() {
        return page == null ? null : page.getOffset();
    }

    /**
     * Method getLimit returns the limit of this AbstractModel object.
     *
     * @return the limit (type Integer) of this AbstractModel object.
     */
    public Integer getLimit() {
        return page == null ? null : page.getLimit();
    }

    /**
     * Method setLimit sets the limit of this AbstractModel object.
     *
     * @param limit the limit of this AbstractModel object.
     */
    public void setLimit(int limit) {
        internalGetPage().setLimit(limit);
    }

    public Integer getPage() {
        return page == null ? null : page.getNumber();
    }

    public void setPage(int page) {
        internalGetPage().setNumber(page);
    }

    public String getOrderBy() {
        return Boolean.TRUE.equals(getDesc()) ? "DESC" : null;
    }

    public Boolean getDesc() {
        return page == null ? null : page.isDesc();
    }

    public void setDesc(boolean desc) {
        internalGetPage().setDesc(desc);
    }

    /**
     * Method getTotal returns the total of this AbstractModel object.
     *
     * @return the total (type Long) of this AbstractModel object.
     */
    public Long getTotal() {
        return page == null ? null : page.getTotal();
    }

    /**
     * Method setTotal sets the total of this AbstractModel object.
     *
     * @param total the total of this AbstractModel object.
     */
    public void setTotal(long total) {
        internalGetPage().setTotal(total);
    }

}

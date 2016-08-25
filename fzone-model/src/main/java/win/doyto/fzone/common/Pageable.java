package win.doyto.fzone.common;

import java.util.List;

/**
 * Model的分页接口
 *
 * @author f0rb, create at 2012-08-28 09:13
 */
public interface Pageable<M> {
    /**
     * 获取对象列表
     *
     * @return 对象列表
     */
    List<M> getList();

    /**
     * 获取分页查询时的偏移量，orm框架会调用此方法
     *
     * @return offset
     */
    Long getOffset();

    /**
     * 获取记录总数.
     *
     * @return 记录总数
     */
    Long getTotal();

    void setTotal(long total);

    Integer getLimit();

    /**
     * 设置分页大小.
     */
    void setLimit(int limit);

    Integer getPage();

    /** 设置当前页数. */
    void setPage(int pageNumber);


    /**
     * Class com.uniweb.web.Page description goes here.
     *
     * @author f0rb, create at 2012-08-28 09:16
     */
    class Page {
        private final int LIMIT_SIZE = 20; //默认分页大小
        private long offset = 0; //分页查询时的偏移量, 由curr和offset确定
        private int number = 1; //从前台传入的用户查看的页号，结合limit确定offset
        private int limit = LIMIT_SIZE; //分页大小
        private long total = 0; //记录总数
        private boolean desc = false; //分页倒序

        public long getOffset() {
            return offset;
        }

        public int getLimit() {
            return limit;
        }

        /**
         * 更改分页大小limit后需要根据当前页面curr修改数据偏移量offset，
         * 分页大小最少为1，即每页至少有一条记录。
         *
         * @param limit 分页大小
         */
        public void setLimit(int limit) {
            if (limit < 1) {
                limit = LIMIT_SIZE;
            }
            if (this.limit != limit) {
                this.limit = limit;
                offset = (getNumber() - 1) * limit;
            }
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            if (total < 0) {
                total = 0;
            }
            this.total = total;
            int pages = getPages();
            // 如果当前页号比总页数还大，要调整当前页号为最大页
            if (number > pages) {
                setNumber(pages);
            }
        }

        public int getNumber() {
            return number;
        }

        /**
         * 设置所要查看或跳转的页面
         * 如果是一个小于1的页号，调整到第一页
         * 如果是一个很大的不存在的页号，会在setTotal()里调整，这里不需要处理
         *
         * @param number 当前页面
         */
        public void setNumber(int number) {
            if (number < 1) {
                number = 1;
            }
            if (this.number != number) {
                this.number = number;
                offset = number > 1 ? (number - 1) * limit : 0;
            }
        }

        /**
         * 根据记录总数total和分页大小limit计算出总页数
         *
         * @return 总页数
         */
        public int getPages() {
            //return (int) Math.ceil((double) total / limit);
            return (int) (total % limit == 0 ? total / limit : total / limit + 1);
        }

        public boolean isDesc() {
            return desc;
        }

        public void setDesc(boolean desc) {
            this.desc = desc;
        }
    }

}

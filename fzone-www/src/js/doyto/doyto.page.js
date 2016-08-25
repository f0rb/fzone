"use strict";

/* global Util */
/* exported Page */

// 分页的处理类
var Page = function (queryFunc) {
    var page = 0, limit = 10, pages = 0, total = 0, qo;
    function SuccessCallback(data) {
        if (data.success) {
            if (typeof data.total === "number") {
                total = data.total;
                var newPages = Math.ceil(total / limit);
                if (pages !== newPages) {
                    pages = newPages;
                    if (page > newPages) {// 当前页号page大于总页数时，是没有数据的，需要修正page然后重新加载
                        page = Math.min(newPages, 1);
                        this && this.load();
                        return;
                    }
                }
                if (!page && pages) {
                    page = 1;
                }
                var p = this;
                p.page = page;
                p.limit = limit;
                p.pages = pages;
                p.total = total;
                p.from = Math.max((page - 1) * limit + 1, 0);
                p.to = Math.min(page * limit, total);
                p.result = data.result;
            }
        } else {
            Util.handleFailure(data);
        }
    }
    this.q = {};
    this.isQueryChanged = function () {
        return !angular.equals(this.q, qo);
    };
    this.load = function (checkQueryChanged) {
        var q = this.q;
        if (checkQueryChanged && angular.equals(this.q, qo)) {
            // 如果设置checkQueryChanged为true, 并且本次查询条件this.q和前次查询条件qo是一样的,
            // 则不执行不必要加载操作.
            return;
        }
        q.page = page || 1;
        q.limit = limit;
        qo = angular.copy(q);
        queryFunc(q, angular.bind(this, SuccessCallback));
        return this;
    };
    this.first = function () {
        if (page > 1) {
            page = 1;
            this.load();
        }
    };
    this.last = function () {
        if (page !== pages) {
            page = pages;
            this.load();
        }
    };
    this.prev = function () {
        if (page > 1) {
            page--;
            this.load();
        }
    };
    this.next = function () {
        if (page < pages) {
            page++;
            this.load();
        }
    };
    this.size = function (size) { // 设置每页显示条数
        limit = size;
        page = Math.min(page, Math.ceil(total / limit));// 当前页数p不能大于总页数
        page = Math.max(page, 1);
        this.load();
    };
    this.goto = function (goto) {
        page = goto;
        this.load();
    };
    this.reset = function () {
        this.q = {};
        page = 1;
        this.load();
    };
};
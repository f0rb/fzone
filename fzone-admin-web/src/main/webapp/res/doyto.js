"use strict";

/* global angular */
/* exported doyto */

var doyto = angular.module('doyto.fzone', []);

"use strict";

/* global Page */
/* exported Crud */

var Crud = function (R, successFunc, errorFunc) {
    var defaultErrorMessage = '访问错误';
    var loading = false;

    function simpleCopy(source) {
        var destination = {}, exclude = 'createTime|createUserId|updateTime|updateUserId|deleted';
        for (var key in source) {
            if (source.hasOwnProperty(key) && exclude.indexOf(key) < 0) {
                destination[key] = source[key];
            }
        }
        return destination;
    }

    var onSuccess = function (data) {
        loading = false;
        if (typeof successFunc === 'function') {
            successFunc(data);
        } else {
            if (!data.success) {
                alert(data.message || defaultErrorMessage);
            }
        }
    };

    var onError = function (ret) {
        loading = false;
        if (typeof errorFunc === 'function') {
            errorFunc(ret);
        } else {
            if (typeof ret === 'object') {
                alert('Status[' + ret.status + ']: ' + (ret.data.message || ret.statusText || defaultErrorMessage));
            } else {
                alert(typeof ret === 'string' ? ret : defaultErrorMessage);
            }
        }
    };

    this.add = function () {
        this.record = {};
    };

    this.edit = function (record) {
        this.record = simpleCopy(record);
    };

    this.view = function (record) {
        this.record = record;
    };

    this.save = function (record) {
        if (loading) {
            return;
        }
        loading = true;
        R.save(record, onSuccess, onError);
    };

    this.remove = function (record, message) {
        if (!confirm(message || "确定要删除这条记录吗?")) {
            return;
        }
        R.remove({}, {id: record.id}, onSuccess, onError);
    };

    this.p = new Page(R.query).load();
};
"use strict";

/* global doyto */

/**
 * @dydoc directive
 * @name dyHref
 * @restrict A
 *
 * @description
 * <a dy-href="logout">退出</a>
 *
 * @element A
 * @param {template} dyHref any string.
 *
 */
doyto.
directive('dyHref', function () {
    return {
        link: function postLink($scope, $elem, $attrs) {
            var elem = $elem[0];
            if (elem.tagName === 'A') {
                elem.style.cursor = 'pointer';
                elem.onclick = function () {
                    location.href = $attrs.dyHref;
                };
            }
        }
    };
});

"use strict";

/* global doyto */

doyto.
filter('regex', function() {
    return function(input, field, regex) {
        if (!input || !input.length) {
            return [];
        }
        var out = [];
        regex = new RegExp(regex);
        for (var i = 0; i < input.length; i++) {
            if (regex.test(input[i][field])) {
                out.push(input[i]);
            }
        }
        return out;
    };
}).
filter('capitalize', function() {
    return function(token) {
        return !!token ? token.charAt(0).toUpperCase() + token.slice(1) : '';
    };
});

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
"use strict";

/* exported Util */

var Util = window.Util || {};
// 分页的处理类
Util.formHttpRequestTransform = function (data) {
    /**
     * The workhorse; converts an object to x-www-form-urlencoded serialization.
     * @param {Object} obj
     * @return {String}
     */
    var param = function (obj) {
        var query = '';
        var name, value, fullSubName, subName, subValue, innerObj, i;

        for (name in obj) {
            if (obj.hasOwnProperty(name) && !name.startsWith("$$")) {
                value = obj[name];

                if (value instanceof Array) {
                    for (i = 0; i < value.length; ++i) {
                        subValue = value[i];
                        fullSubName = name + '[' + i + ']';
                        innerObj = {};
                        innerObj[fullSubName] = subValue;
                        query += param(innerObj) + '&';
                    }
                } else if (value instanceof Object) {
                    for (subName in value) {
                        if (value.hasOwnProperty(subName)) {
                            subValue = value[subName];
                            fullSubName = name + '[' + subName + ']';
                            innerObj = {};
                            innerObj[fullSubName] = subValue;
                            query += param(innerObj) + '&';
                        }
                    }
                } else if (value !== undefined && value !== null) {
                    query += encodeURIComponent(name) + '=' + encodeURIComponent(value) + '&';
                }
            }
        }
        return query.length ? query.substr(0, query.length - 1) : query;
    };

    return angular.isObject(data) && String(data) !== '[object File]' ? param(data) : data;
};

Util.escapeHTML = function(text) {
    return !text ? "" : text.replace(/[\"'\/<>]/g, function (a) {
        return {'"': '&quot;', "'": '&#39;', '/': '&#47;', '<': '&lt;', '>': '&gt;'}[a];
    });
};

Util.capitalize = function (s) {
    return typeof s === 'string' ? s.charAt(0).toUpperCase() + s.slice(1) : s;
};

Util.handleFailure = function (data) {
    if (data && !data.success) {
        if (data.code === '0001') {
            location.href = 'login?redirect=' + encodeURIComponent(location.href);
        } else {
            alert(data.message || '访问错误');
        }
    }
};
/**
 * Email to Site Map.
 * 根据邮箱的后缀打开邮箱登录页面.
 *
 * User: f0rb
 * Time: 2012-04-25 14:10
 */
(function (w) {
    'use strict';
    w.email2site = function (s) {
        w.open({
                'gmail.com': 'http://gmail.com',
                'hotmail.com': 'http://www.hotmail.com',
                'live.com': 'http://www.hotmail.com',
                '126.com': 'http://mail.126.com',
                '163.com': 'http://mail.163.com',
                'sina.com': 'http://mail.sina.com.cn',
                'sina.cn': 'http://mail.sina.com.cn',
                'qq.com': 'http://mail.qq.com',
                'vip.qq.com': 'http://mail.qq.com',
                'foxmail.com': 'http://mail.qq.com',
                'yahoo.com': 'http://mail.yahoo.com',
                'yahoo.com.tw': 'http://mail.yahoo.com.tw',
                'yahoo.com.hk': 'http://mail.yahoo.com.hk',
                'sohu.com': 'http://mail.sohu.com',
                'yeah.net': 'http://www.yeah.net',
                'yahoo.cn': 'http://mail.yahoo.cn'
            }[s.substring(s.indexOf('@') + 1)] || 'http://' + s
        );
    };
}(window));
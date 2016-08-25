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
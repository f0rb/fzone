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

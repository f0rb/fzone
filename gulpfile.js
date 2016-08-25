// 引入 gulp
var gulp = require('gulp');

// 引入组件
var jshint = require('gulp-jshint'),
    concat = require('gulp-concat'),
    uglify = require('gulp-uglify'),
    clncss = require('gulp-clean-css'),
    rename = require('gulp-rename'),
    os     = require('os'),
    //fs     = require('fs'),
    //seq = require('run-sequence'),
    path = require('path'),
    sass = require('gulp-sass'),

    hint = {
        "node": true,
        "browser": true,
        "esnext": true,
        "bitwise": true,
        "camelcase": true,
        "curly": true,
        "eqeqeq": true,
        "expr": true,
        "immed": true,
        "devel": true,
        "indent": 4,
        "latedef": true,
        "newcap": true,
        "noarg": true,
        //"quotmark": "single",
        "undef": true,
        "unused": true,
        "strict": true,
        "trailing": true,
        "smarttabs": true,
        "multistr": true,
        "globals": {
            "$": false,
            "angular": false
        }
    },

    end;

var bowerDir = '/Users/Yuan/bower/';
var www = './fzone-www/src/';

var doyto = {};
doyto.res = www + 'js/';
// doyto.target = www + 'target/admin/res/';
doyto.js = www + 'js/doyto/*.js';

var admin = {};
admin.root = './fzone-admin-web/';
admin.res = admin.root + 'src/main/webapp/res/';
admin.target = admin.root + 'target/admin/res/';
admin.www = './fzone-www/src/';
admin.js = admin.www + 'js/admin/*.js';
admin.scss = admin.www + 'sass/*.scss';

gulp.task('admin:pre', function () {
    gulp.
    src([
        bowerDir + 'angular/angular*',
        bowerDir + 'angular-ui-router/release/angular-ui-router*',
        bowerDir + 'angular-resource/angular-resource*',
        bowerDir + 'angular-touch/angular-touch*',
        bowerDir + 'angular-resizable/angular-resizable*'
    ]).
    pipe(gulp.dest(admin.res + 'lib/angular'));
    gulp.src(bowerDir + 'jquery/dist/*').pipe(gulp.dest(admin.res + 'lib/jquery'));
    gulp.src(bowerDir + 'bootstrap/dist/**').pipe(gulp.dest(admin.res + 'lib/bootstrap'));
    gulp.src(bowerDir + 'font-awesome/**').pipe(gulp.dest(admin.res + 'lib/font-awesome/'));
});

gulp.task('admin:js', function () {
    try {
        gulp.src(admin.js)
            .pipe(jshint(hint))
            .pipe(jshint.reporter('default'));
        //admin项目
        gulp.src(admin.js)
            .pipe(concat('admin.js'))
            .pipe(gulp.dest(admin.res))
            .pipe(gulp.dest(admin.target))
            .pipe(rename({suffix: '.min'}))
            .pipe(uglify())
            .pipe(gulp.dest(admin.target))
            .pipe(gulp.dest(admin.res));
    } catch (e) {
        console.log(e.toString());
    }
});

gulp.task('admin:sass', function () {
    return gulp
        .src(admin.scss)
        .pipe(sass()) // Using gulp-sass
        .pipe(rename('admin.css'))
        .pipe(gulp.dest(admin.res))
        .pipe(gulp.dest(admin.target))
        .pipe(rename({suffix: '.min'}))          //- 合并后的文件名
        .pipe(clncss())                          //- 压缩处理成一行
        .pipe(gulp.dest(admin.res))            //- 输出文件本地
        .pipe(gulp.dest(admin.target));
});

gulp.task('admin', ['admin:js', 'admin:sass']);

gulp.task('doyto:js', function () {
    try {
        gulp.src(doyto.js)
            .pipe(jshint(hint))
            .pipe(jshint.reporter('default'))
            .pipe(concat('doyto.js'))
            .pipe(gulp.dest(admin.res))
            .pipe(gulp.dest(admin.target))
            .pipe(gulp.dest(doyto.res))
            .pipe(rename({suffix: '.min'}))
            .pipe(uglify())
            .pipe(gulp.dest(admin.res))
            .pipe(gulp.dest(admin.target))
            .pipe(gulp.dest(doyto.res));
    } catch (e) {
        console.log(e.toString());
    }
});

// 默认任务
gulp.task('default', ['admin', 'doyto:js'], function () {
    // 监听文件变化
    gulp.watch(admin.scss, ['admin:sass']);
    gulp.watch(admin.js, ['admin:js']);

    gulp.watch(doyto.js, ['doyto:js']);
});

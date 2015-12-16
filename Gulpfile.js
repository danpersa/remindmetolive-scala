var gulp, sass, watchify, util;

gulp = require('gulp');
util = require('gulp-util');
sass = require('gulp-sass');
watchify = require('watchify');
var sourcemaps = require('gulp-sourcemaps');
var concat = require('gulp-concat');

gulp.task('default', ['compile-sass', 'compile-scss', 'css', 'js', 'images', 'templates', 'post-images']);

gulp.task('watch', ['watch-sass', 'watch-scss', 'watch-templates', 'watch-post-images']);

gulp.task('watch-sass', function () {
    gulp.watch('assets/css/**/*.sass', ['compile-sass']);
});

gulp.task('watch-scss', function () {
    gulp.watch('assets/css/**/*.scss', ['compile-scss']);
});

gulp.task('watch-templates', function () {
    gulp.watch('assets/templates/**', ['templates']);
});

gulp.task('watch-post-images', function () {
    gulp.watch('assets/post-images/**', ['post-images']);
});

gulp.task('compile-sass', function () {
    gulp.src('assets/css/**/*.sass')
        //.pipe(sourcemaps.init())
        .pipe(sass({indentedSyntax: true, errLogToConsole: true}))
        //.pipe(sourcemaps.write())
        .pipe(gulp.dest('target/sass'));
});

gulp.task('compile-scss', function () {
    gulp.src('assets/css/**/*.scss')
        //.pipe(sourcemaps.init())
        .pipe(sass({indentedSyntax: false, errLogToConsole: true}))
        //t.pipe(sourcemaps.write())
        .pipe(gulp.dest('target/scss'));
});

gulp.task('css', function () {
    gulp.src([
        'assets/css/bootstrap.min.css',
        'assets/css/main.css',
        'assets/css/blue.css',
        'assets/css/owl.carousel.css',
        'assets/css/owl.transitions.css',
        'assets/css/animate.min.css',
        'assets/fontello.css',
        'target/sass/**.css',
        'target/scss/**.css'
    ])
        .pipe(concat("application.css"))
        .pipe(gulp.dest("target/assets"))
});

gulp.task('js', function () {
    gulp.src([
        'assets/js/jquery.min.js',
        'assets/js/jquery.easing.1.3.min.js',
        'assets/js/bootstrap.min.js',
        'assets/js/bootstrap-hover-dropdown.min.js',
        'assets/js/skrollr.min.js',
        'assets/js/skrollr.stylesheets.min.js',
        'assets/js/waypoints.min.js',
        'assets/js/waypoints-sticky.min.js',
        'assets/js/owl.carousel.min.js',
        'assets/js/jquery.isotope.min.js',
        'assets/js/jquery.easytabs.min.js',
        'assets/js/viewport-units-buggyfill.js',
        'assets/js/scripts.js'
    ])
        .pipe(sourcemaps.init())
        .pipe(concat("application.js"))
        .pipe(sourcemaps.write("."))
        .pipe(gulp.dest("target/assets"))
});

gulp.task('templates', function () {
    gulp.src([
          'assets/templates/**'
      ])
      .pipe(gulp.dest("target/assets/templates"))
});

gulp.task('post-images', function () {
    gulp.src([
          'assets/post-images/**'
      ])
      .pipe(gulp.dest("target/assets/images"))
});

gulp.task('images', function () {
    gulp.src([
        'assets/images/**'
    ])
        .pipe(gulp.dest("target/assets"))
});
'use strict';

const gulp = require('gulp');
const concat = require('gulp-concat');
const sass = require('gulp-sass');
const postcss = require('gulp-postcss');
const babel = require('gulp-babel');
const cssnext = require('postcss-cssnext');
const watch = require('gulp-watch');
const cleanCSS = require('gulp-clean-css');
const uglify = require('gulp-uglify');

gulp.task('vendor-style', () => {
  gulp.src(['../node_modules/animate.css/animate.css',
    '../node_modules/font-awesome/css/font-awesome.css',
    '../node_modules/simple-line-icons/css/simple-line-icons.css',
    '../node_modules/weather-icons/css/weather-icons.css',
    './lib/stylesheets/*.css'])
  .pipe(concat('vendor.min.css'))
  .pipe(cleanCSS({level: {1: {specialComments: 0}}}))
  .pipe(gulp.dest('../public/stylesheets'));
});

gulp.task('web-font', () => {
  gulp.src(['../node_modules/font-awesome/fonts/*',
    '../node_modules/simple-line-icons/fonts/*',
  ])
  .pipe(gulp.dest('../public/fonts'));
});

gulp.task('scss', () => {
  var processors = [
      cssnext()
  ];
  return gulp.src('./stylesheets/*.scss')
    .pipe(sass())
    .pipe(postcss(processors))
    .pipe(concat('application.min.css'))
    .pipe(gulp.dest('../public/stylesheets'));
});

gulp.task('prepare-scripts', () => {
  gulp.src(['./lib/prepare/*.js'])
  .pipe(concat('prepare.min.js'))
  .pipe(uglify())
  .pipe(gulp.dest('../public/scripts'));
});

gulp.task('vendor-scripts', () => {
  gulp.src(['../node_modules/typeahead.js/dist/*.js',
    './lib/scripts/*.js'])
  .pipe(concat('vender.min.js'))
  .pipe(uglify())
  .pipe(gulp.dest('../public/scripts'));
});

gulp.task('scripts', () => {
  gulp.src(['./scripts/*.js'])
  .pipe(concat('app.min.js'))
  // .pipe(babel())
  // .pipe(uglify())
  .pipe(gulp.dest('../public/scripts'));
});

gulp.task('watch', () => {
  gulp.watch('./stylesheets/*.scss', ['scss']);
  gulp.watch('./scripts/*.js', ['scripts']);
});

gulp.task('build', ['scss', 'vendor-style', 'prepare-scripts', 'vendor-scripts', 'web-font', 'scripts']);
gulp.task('default', ['scss', 'watch', 'vendor-style', 'web-font']);


'use strict'

###*
 # @ngdoc function
 # @name angularAppApp.controller:BooksCtrl
 # @description
 # # BooksCtrl
 # Controller of the angularAppApp
###
angular.module('angularAppApp')
  .controller 'BooksCtrl', ($scope, $http, Calendar) ->

    $scope.start = moment("2014-01-01")
    $scope.end = moment("2015-06-01")

    months = for i in _.range(0, 18)
      moment($scope.start).add(i, 'month').format("MMMM YYYY")

    $scope.months = months

    $scope.books = []
    $scope.authors = []
    $scope.$watch 'books', =>
      $scope.authors = Calendar.monthlyByAuthor($scope.books)

    $http.get("http://localhost:3001/user/2003928/books?shelf=read").success (data) ->
      console.log data
      $scope.books = data


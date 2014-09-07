'use strict'

describe 'Controller: BooksCtrl', ->

  # load the controller's module
  beforeEach module 'angularAppApp'

  BooksCtrl = {}
  scope = {}

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope) ->
    scope = $rootScope.$new()
    BooksCtrl = $controller 'BooksCtrl', {
      $scope: scope
    }

  describe 'calendar generations', ->
    it 'generates an empty array when no books are present', ->
      scope.books = []
      expect(scope.booksByAuthor().length).toBe 0

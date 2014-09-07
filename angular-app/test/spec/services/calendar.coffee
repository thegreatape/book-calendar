'use strict'

describe 'Service: calendar', ->

  # load the service's module
  beforeEach module 'angularAppApp'

  # instantiate service
  Calendar = {}
  beforeEach inject (_Calendar_) ->
    Calendar = _Calendar_

  it 'returns an empty array when given 0 books', ->
    expect(Calendar.monthlyByAuthor([])).toEqual []

  it 'returns a single entry with all months when given one book', ->
    author = {id: 123, name: "Kameron Hurley"}
    book =
      authors: [author],
      title: "The Mirror Empire",
      publication_date: "2014-08-26"

    expect(Calendar.monthlyByAuthor([book])).toEqual [
        author: author
        months:
          "August 2014": [book]
    ]

  it 'returns a single entry with all months when given multiple books by the same author', ->
    author = {id: 123, name: "Kameron Hurley"}
    mirror_empire =
      authors: [author],
      title: "The Mirror Empire",
      publication_date: "2014-08-26"

    gods_war =
      authors: [author],
      title: "God's War",
      publication_date: "2011-01-13"

    expect(Calendar.monthlyByAuthor([mirror_empire, gods_war])).toEqual [
        author: author
        months:
          "January 2011": [gods_war]
          "August 2014": [mirror_empire]
    ]

  it 'returns multiple entries with all months when given multiple books by different authors', ->
    kameron_hurley = {id: 123, name: "Kameron Hurley"}
    mirror_empire =
      authors: [kameron_hurley],
      title: "The Mirror Empire",
      publication_date: "2014-08-26"


    peter_watts = {id: 124, name: "Peter Watts"}
    echopraxia =
      authors: [peter_watts],
      title: "Echopraxia",
      publication_date: "2014-08-26"

    bookCalendar = Calendar.monthlyByAuthor([mirror_empire, echopraxia])
    expect(bookCalendar).toContain
      author: kameron_hurley
      months:
        "August 2014": [mirror_empire]

    expect(bookCalendar).toContain
      author: peter_watts
      months:
        "August 2014": [echopraxia]

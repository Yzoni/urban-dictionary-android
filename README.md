## Native App Studio 2016

### Urban Dictionary
Native android app which uses the Urban Dictionary API to lookup words. Search for a word to get
its definition and examples. It is also possible to share a result using the share button on each
item. Firebase signup is required to save personal search history locally. A search history item can
be removed by swiping the item to the left or the right. App saves its state when switching apps or
when screen rotates.

### Screenshots
![1](/screenshots/search_result.png?raw=true "screen_result")

![1](/screenshots/search_history.png?raw=true "screen_history")

### Technical description
The app uses one main activity. Two fragments are swapped out for each other to display search
history or search results.
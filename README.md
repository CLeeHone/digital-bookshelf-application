# Digital Bookshelf Application
Allows users to retrieve, create, store, and rate books they have read on their phone. This is a lightweight application that stores user data locally in a SQLite database. 
## Author
* Chloe Lee-Hone
## Class Description
* **Book**: POJO of a book.
* **BookBuilder**: Interface used in the ConcreateBookBuilder class.
* **BookAddingActivity**: Allows users to add a book to their bookshelf following a search.
* **BookSearchList**: Provides a list of results following a search for a specific book.
* **BookShelf**: Displays all books saved and rated by the user. Retrieves data from a local SQLite database.
* **BookshelfDetailsFragment**: Displays a stored book's details, including its author, year, description, and the user's rating
* **ConcreteBookBuilder**: Allows the user to create a custom book using the Builder pattern.
* **EmptyActivity**: Used for tablet compatibility.
* **MainActivity**: Application's landing page.
* **MyOpener**: Allows for the retrieval of stored items in the SQLite database.
## Application Examples
| Emulator | Demo |
|----------|------|
| ![image](https://github.com/CLeeHone/android-bookshelf-application/assets/67878819/1667887f-cd91-4834-ab67-a4965e5703c9) | ![image](https://github.com/CLeeHone/android-bookshelf-application/assets/67878819/7c02bb37-9d75-428a-9df7-84a02d075fef) |
| ![image](https://github.com/CLeeHone/android-bookshelf-application/assets/67878819/a223668e-d59b-4f24-a3e1-959d2e657ee1) | ![image](https://github.com/CLeeHone/android-bookshelf-application/assets/67878819/9427c89e-daa4-4970-93a5-18e0980743fe) |
## TODO
* Remove requirement for double-click in drawer
* Include navbar in all activities
* Explore integrating OCR technology to allow users to scan book information
* Polish the application, change styling for better UX

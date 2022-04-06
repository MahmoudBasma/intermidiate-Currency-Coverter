# intermidiate-Currency-Coverter
This app is intended to be a rate exchange app. 
The app uses 4 rates to concert money amounts from LBP to USD and vice versa. 
The rates are black marker (sell and buy) rates, the bank rate and the official rate annonced by BDL
The rates are scraped out of a website using the simple HTML DOM parser in php.

The open consist of 4 pages. The first page is the home page. It has an image of some dollars and lebanese pounds. It also has a small text and the 4 rates displayed.
There are also 3 buttons. 2 in a menu bar and one at the buttom. 

the menu bar first button is a help me or info button which will redirect you to the official website where the rates where retireived. 
THe second button is a history button. This button will show you the transactions that were done before and stored in the database.

Finally the bottom bottun is a converting button which will redirect ypu tp the calculator activity.
In the calculator activity you will need to insert the amount, select the currenct and select the rate and it will convert them accordingly. 

After converting. It will post the data into db_post.php api using a get request with url attributes. 

Finally, the MySQL database designed has one table called conversions. 
THe table had 4 fields: id(primary key), amount, rate, currency.s

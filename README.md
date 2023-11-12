# Requests

The API endpoints can be reached from the following URL:
<https://comp-413-clientapi-dot-rice-comp-539-spring-2022.uk.r.appspot.com/api/>.

All requests respond with a standard Http status code and a string message.
Status codes 400-499 indicate a client error; codes 500-599 indicate a server error.

## Login Requests
These requests require basic user information to perform basic user tasks such
as creating a new user with _login/register_ and creating a new active session
for a user with _login/login_.

### Login POST@login/login

Request Body
- user: str, alias the new user wishes to associate with their account.
- pass: str, key to login to user account.

Response Header
- Set-Cookie: STSESSIONID=portfolioId-randomLong-timestamp, Max-Age=3600.

Success: 200: "Login Successful!"

Failure: 400: "Failed to register the user."

### Register POST@login/register
Request Body
- user: str, alias the new user wishes to associate with their account.
- pass: str, key to log in to user account.
- email: str, email used for account recovery.

Success: 201: "User successfully registered."

Failure: 400: "Failed to register the user."

## Order Requests

These requests require order information and the sessionID of the requesting
user.

### Place a Market Order POST@order/placeMarketOrder
Request Body
-  a
Response Body
### Cancel Order DELETE@order/cancelOrder/{sessionId}&{orderId}
Request Path: sessionId&orderId
- sessionID: unique cookie corresponding to a user's active session. This
indicates to the server who is executing the request with userId and login
timestamp info bundled.
- orderId: unique ID associated with an order. This ID is returned by any
request that places an order and should be sent back upon cancellation.



## Dashboard Requests

### Get Portfolio Value GET@dashboard/getPFValue/{sessionId}
_NOT IMPLEMENTED_

Request Body:
- sessionID: str, sessionID(cookie value) of the current logged-in session

Response:
- 

### Get Total Cash Amount GET@dashboard/getCash/{sessionId}
_NOT IMPLEMENTED_

Request:
- sessionID: str, sessionID(cookie value) of the current logged-in session

Response:
- 

### Get Current Stock Price GET@dashboard/getStock/{symbol}
Request Path:
- symbol: str, the symbol of the relevant asset (e.g., stock ticker GOOG)

Response Body:
- price: float, price of stock

Success: 200: "Stock data successfully retrieved."
Failure: 400: "Failed to fetch stock data for requested asset: {symbol}"


### Get Stock Historical Data GET@dashboard/getStockHistory/{symbol}&{range}
Request Body: 
- symbol: str, the symbol of the relevant asset (e.g., stock ticker GOOG)
- range: str, the time range over which financial data are retrieved for the asset.
The range must be one of the following: [DY1, WK1, MO1, MO6, YTD, YR1, YR5, ALL].
Response:

Response Body:
- a

### Get All User Transaction History GET@dashboard/getTransactionHistory/{sessionId}
_NOT IMPLEMENTED_

### Get All User Pending Order History GET@dashboard/getPendingOrders/{sessionId}
_NOT IMPLEMENTED_


### Get All User Holding GET@dashboard/getHoldings/{sessionId}
_NOT IMPLEMENTED_
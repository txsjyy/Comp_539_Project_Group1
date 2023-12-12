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
- username: str, alias the new user wishes to associate with their account.
- password: str, key to login to user account.
- email: str, email associated with the account for recovery.

Response Header
- Set-Cookie: STSESSIONID=portfolioId-randomLong-timestamp;Max-Age=3600.
  Both `portfolioId` and `randomLong` will be strings of non-negative numbers if they are numbers (`portfolioId` need not be a number).
  
    This is a unique cookie corresponding to a user's active session. This
indicates to the server who is executing the request with userId and login
timestamp info bundled.

Success: `200 OK` "Login Successful!"

Failure: `400 BAD_REQUEST` "Failed to log user in - credentials did not match."

### Register POST@login/register

Request Body
- username: str, alias the new user wishes to associate with their account.
- password: str, key to log in to user account.
- email: str, email associated with the account for recovery.

Success: `201 CREATED` "User successfully registered."

Failure can occur on the request lacking any of the required parameters or because a user of the provided username already exists. Note that a user is allowed to create multiple usernames with the same email - this simulates having multiple portfolios or accoutns.

Failure: `400 BAD_REQUEST`
- username clash: "Failed to register the user - username clash."
- no username/password/email: "Failed to register the user - no \<username/password/email> provided."

## Order Requests

These requests require order information and the sessionID of the requesting
user.

### Place a Order POST@order/placeOrder

Request Header
- Cookie: STSESSIONID=portfolioId-randomLong-timestamp;Max-Age=3600

Request Body
-  type: str, one of "STOP","LIMIT", or "MARKET" for the associated order type
-  ticker: str, symbol associated with requested asset
-  quantity: int, amount of asset requested
-  side: str, either "BUY" or "SELL" for buy orders or sell orders, respectively
-  price: float, relevant price for a Limit Order or Stop Order

Success: `201 CREATED` Order successfully placed.

Failure: `400 BAD_REQUEST` Failed to make an order.

### Cancel Order DELETE@order/cancelOrder/{orderId}

Request Path: orderId
- orderId: unique ID associated with an order. This ID is returned by any
request that places an order and should be sent back upon cancellation.

Request Header
- Cookie: STSESSIONID=portfolioId-randomLong-timestamp;Max-Age=3600
  
Success: `200 OK` "Order successfully cancelled."

Failure `400 BAD_REQUEST` "Failred to cancel the request order: \<orderId>."



## Dashboard Requests

### Get Portfolio Value GET@dashboard/getPFValue

Request Header
- Cookie: STSESSIONID=portfolioId-randomLong-timestamp;Max-Age=3600

Response:
- Float, current total value of a portfolio's holdings.

### Get Total Cash Amount GET@dashboard/getCash

Request Header
- Cookie: STSESSIONID=portfolioId-randomLong-timestamp;Max-Age=3600

Response:
- Float, cash held by user.

### Get Current Stock Price GET@dashboard/getStock/{symbol}
Request Path:
- symbol: str, the symbol of the relevant asset (e.g., stock ticker GOOG)

Response Body:
- price: float, price of stock

Success: `200 OK` "Stock data successfully retrieved."
Failure: `400 BAD_REQUEST` "Failed to fetch stock data for requested asset: \<symbol>"


### Get Stock Historical Data GET@dashboard/getStockHistory/{symbol}&{range}

_NOT IMPLEMENTED_

Request Body: 
- symbol: str, the symbol of the relevant asset (e.g., stock ticker GOOG)
- range: str, the time range over which financial data are retrieved for the asset. The range must be one of the following as strings: [DY1, WK1, MO1, MO6, YTD, YR1, YR5, ALL].


### Get All User Transaction History GET@dashboard/getTransactionHistory

Request Header
- Cookie: STSESSIONID=portfolioId-randomLong-timestamp;Max-Age=3600

Response `200 OK`
- `List` of `Order`:
  - id, str: \<portfolioId>_\<symbol>
  - portfolioId, str
  - symbol, str
  - tradeType, str either "BUY" or "SELL"
  - orderType, str one of "MARKET", "LIMIT", or "STOP"
  - statusType, str "FILLED" only
  - quantity, int
  - price, float
  - timestamp, str

### Get All User Pending Order History GET@dashboard/getPendingOrders

Request Header
- Cookie: STSESSIONID=portfolioId-randomLong-timestamp;Max-Age=3600

Response `200 OK`
- `List` of `Order`:
  - id, str: \<portfolioId>_\<symbol>
  - portfolioId, str
  - symbol, str
  - tradeType, str either "BUY" or "SELL"
  - orderType, str one of "MARKET", "LIMIT", or "STOP"
  - statusType, str "PENDING" only
  - quantity, int
  - price, float
  - timestamp, str

### Get All User Cancelled Order History GET@dashboard/getCancelledOrders

Request Header
- Cookie: STSESSIONID=portfolioId-randomLong-timestamp;Max-Age=3600

Response `200 OK`
- `List` of `Order`:
  - id, str: \<portfolioId>_\<symbol>
  - portfolioId, str
  - symbol, str
  - tradeType, str either "BUY" or "SELL"
  - orderType, str one of "MARKET", "LIMIT", or "STOP"
  - statusType, str "CANCELLED" only
  - quantity, int
  - price, float
  - timestamp, str

### Get All User Holding GET@dashboard/getHoldings

Request Header
- Cookie: STSESSIONID=portfolioId-randomLong-timestamp;Max-Age=3600

Response `200 OK`
- `List` of `Holding`:
  - id, str: \<portfolioId>_\<symbol>
  - portfolioId, str
  - symbol, str
  - quantity, int
  - lastPurchasePrice, float
  - lastPurchaseDate, int
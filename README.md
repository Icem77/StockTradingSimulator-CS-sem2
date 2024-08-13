# StockTradingSimulator-CS-sem2

📈 Java implementation a trading system that, within each turn, queries all investors (in a random order for each turn) about their own investment decisions. Specifically, within one turn, investors can:
  * Query the system for the current turn number,
  * Query the trading system for the turn number and price of the last transaction for the specified company's shares (any number of times),
  * For a selected listed company, make a decision to place at most one buy or sell order for its shares.

📈 After accepting investment decisions from all investors within one turn, the trading system executes orders according to its rules.

📈 The Stock Exchange handles the trading of company shares. The stock trading system allows investors to place buy or sell orders. An order is a declaration of intent to buy or sell shares of a selected company. Each order must include:

📈 Order type: buy/sell,
  * Stock identifier (a non-empty ASCII string A-Z, no longer than 5 characters),
  * Number of shares (a positive integer),
  * Price limit (a positive integer).

📈 We assume that a single order can express the intent to buy/sell shares of one company with a price limit (upper limit in the case of a buy order, lower limit in the case of a sell order). A transaction occurs when there is a match between buy and sell orders for the same company's shares, i.e., when the buy order price is equal to or higher than the sell order price.

📈 In our trading system, time is measured in turns. The order of execution is primarily determined by the price set by the investor (a buy order with the highest limit is matched to a sell order with the lowest limit), and then by the turn in which the order was placed. In the case of multiple orders placed in the same turn, the order of their submission decides. When executing orders, transactions are made at a price equal to the limit price of the order that was placed earlier, and in the case of orders placed in the same turn, the order of submission decides.

📈 Investors have several options to specify the order's validity period:
  * Immediate order - the order must be executed, at least partially, in the same turn in which it was added to the order book. The unexecuted part of the order is eliminated by the system.
  * Order with no specific validity period - remains in the system until it is fully executed (as a result of one or more transactions).
  * Order valid until the end of a specified turn - the order remains in the system until the end of the n-th turn unless it is fully executed earlier.

📈 Two types of investors, using different investment strategies, are implemented:
  * RANDOM - an investor making random investment decisions (implement any strategy - e.g., random buy/sell order type, random company, random number of shares, random price limit). Random selection is done according to the conditions specified in the text.
  * SMA - an investor making decisions based on technical analysis of individual shares using the Simple Moving Average (SMA n) - the moving arithmetic average of the price over the last n turns (in our task, we use n=5 and n=10). The investor starts making decisions based on the signal from the moment when SMA 10 can be calculated (at least from the 10th turn).

📈 PROGRAM INPUT FILE EXAMPLE: (lines starting with # should be ignored):
# 6 investors: 4x RANDOM, 2x SMA
R R R R S S
# 3 stocks with latest transaction value
APL:145 MSFT:300 GOOGL:2700
# starting amount of money and stocks for investors (same for everyone) 
100000 APL:5 MSFT:15 GOOGL:3

📈 PROGRAM OUTPUT:
Program verifies the correctness of the input data, particularly ensuring that the portfolio contains stocks that are being traded, and should print the final 
state of the portfolio for each investor to the standard output after completing all simulation turns. Events occurring on the stock exchange are printed in
real-time as they happen.

❗In the code, both variable names and comments are in Polish because this program was one of the projects for the second semester of computer science studies. Sorry for the inconvenience.❗️

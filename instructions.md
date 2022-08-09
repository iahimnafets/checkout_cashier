Checkout Kata
Implement the code for a supermarket checkout that calculates the total price of a number of items.
In a normal supermarket, items are identified by ‘stock keeping units’ or ‘SKUs’. In our store, we will use
individual letters of the alphabet, A, B, C etc, as the SKUs. Our goods are priced individually. In addition,
some items are multipriced: buy n of them and which will cost you y. For example, item A might cost 50
pence individually but this week we have a special offer where you can buy 3 As for £1.30.
This week’s prices are the following:


ITEM    UNIT-PRICE    SPECIAL-PRICE
A         2.50        3 for 6 pounts          
B         4           2 for 7 pounts 
C         10          3 for 25 pounts
D         3           2 for 4 pounts


Our checkout accepts items in any order so if we scan a B, then an A, then another B, we will recognise
the two B’s and price them at 45 (for a total price so far of 95).
Extra points: Because the pricing changes frequently we will need to be able to pass in a set of pricing
rules each time we start handling a checkout transaction.


What we are looking for:

- Simple but extensible design
- ‘Clean’, readable code
- Test 
- Handling of edge cases
- Use of SOLID design principles

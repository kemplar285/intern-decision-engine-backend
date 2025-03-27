# Ticket 101, back-end part. Overview.

The overview of front-end part can be found by the following link: https://github.com/kemplar285/intern-decision-engine-frontend

Firstly, I want to point a couple of moments I have noticed while working with this ticket.
1. There is no clear indication in the task on how to calculate a person's credit modifier.
The intern has provided a mock implementation based on the last 4 digits of an ID code. 
It contradicts the example from the task, where a person with ID 49002010998 has 1000 credit score. 
In current implementation 1000 credit score can only be achieved with ID codes enging withing the range of 7500-9999. 
I did not change this part, as I don't have any better examples.

2. Some constants, for example maximum loan period, were clearly not in line with the requirements. 
It was noticed both in back-end and front-end parts of the task. 

3. About the loan amounts. The task required us to return a biggest loan amount possible, regardless of the sum the client asked for.
From credit score calculation formula (credit score = ((credit modifier / loan amount) * loan period) / 10), we can derive that the loan period is the biggest factor here. 
So the highest loan amounts can be always achieved by increasing loan period, until the loan amount caps at 10000. The automatic tests provided did not reflect this rule.
For example, test on segment 2 suggested that if client asks for a loan of 4000 EUR and 12 months of loan period, the return values should be 3600 EUR and 12 months. In my opinion, it clearly contradicts the task. 
I suggest the return values of 4200 EUR and 40 months. Both return values have credit score of 0.1, which is in line with the requirements, but the second variant allows the customer to receive the amount needed.


Goods:
1. Error handling. I moved it from controller to controller advice, but it did work the old ways as well.
It's nice to see custom exceptions used.
2. Use of constants instead of magical numbers within the code.
3. Javadoc comments.
4. Tests implemented
5. Naming

Bads:
1. All decision engine related logic was aggregated into one big class.
It clearly violates SOLID principles (SRP, OCP).
2. The lack of communication. Some ideas used in the implementation were not mentioned in the task and it would have been nice to see explanation of decision made in the readme. This includes returned loan amounts and periods from tests.
3. Credit score calculation formula is invalid
4. Some constants contradicted the requirements
# Features

1. Emojis support

Problem: Emoji is made of 2 Java chars 2 x 16bit. Current
implementation keeps a char in each Cell. 

# Solution 1:

Adding flags isLowSurrogate & isHighSurrogate to each Cell

Implementation:
- add flags to Cell class
- instead of iterating over array of chats we need to go through stream of code points
- extract low and high surrogate and save that 
- when reading check if some of those flags is set to true then check neighboring cells for the second half

Pros: 
- Base Type of Cell -> Character will stay the same as well as all code that uses it

Cons: 
- There will be a problem with reading terminal content. A lot of checks for those flags will be needed
- getCharacter will return ??? (half of emoji cannot be returned)


# Solution 2:

Storing String in each cell

Implementation:
- Change Character -> String
- instead of iterating over array of chats we need to go through stream of code points
- extract code point and save it to String
- getCharacter will return String
- block next cell by setting flag isBlocked=true, emoji takes 2 Cells so we need to block inserting a char to next cell

Pros:
- Emojis can be saved/read out of the box

Cons:
- Change of base type is needed
- getCharacter will return String instead of Character

# Solution 3:
Storing int with a codePoint

Implementation:
- Change Character -> int
- instead of iterating over array of chats we need to go through stream of code points
- extract code point and save it to int
- getCharacter will return String
- block next cell by setting flag isBlocked=true, emoji takes 2 Cells so we need to block inserting a char to next cell


Pros:
- int uses less memory than object -> String
- one value per each letter/number/emoji etc

Cons: 
 - Conversion from int to String is needed when displaying buffer content -> Second solution uses String so whats the point
 - Change of base type is needed
 - getCharacter will return String instead of Character


# Final Decision

There is no perfect Solution for this problem. Storing single Character in each Cell is no longer possible if we want to support Emojis.
Despite the fact that in requirements there is "Get character at position (from screen and scrollback)",
I see no possibility for storing emoji in either char primitive or wrapper class Character. Change of the base type is unavoidable.
After taking all proposed solutions into an account, I think the best it to go with String as base type.

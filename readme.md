# Terminal Buffer — Design Decisions & Trade-offs

## Core Design

### Data Structure
The buffer consists of a List of Lines where each LIne is a fixed-width array of Cells objects.
The screen holds the last N visible lines. Oldest lines are moved to scrollback when screen reaches max size

### Why ArrayDeque for scrollback
It gives O(1) insertion at the tail and O(1) removal from the head
New lines are added at the end, oldest lines gets removed from the front when the scrollback is full.

### Insert — linear shift instead of per-line shift
My initial approach for insert focused on overflowing character - the one that gets oved to the next line after reaching line width max
It required propagating similar behavior to all lines below. For single characters it was quite okay, but when emoji came into picture -
handling 2 cells wide emojis and its overflow  got complicated.

### Resize
- Increasing height: new lines are pulled from scrollback if available, otherwise empty lines are added.
  This preserves history and feels natural — it releases previously hidden content.
- Decreasing height: lines from the top of the screen are pushed to scrollback.
  This is a simple strategy — content is not lost, just moved to history.
- Only resizing height is currently supported

---

## Feature: Emoji

### Problem
Emoji consists of 2 Java chars (2 x 16-bit). The original implementation stored a single Character
in each Cell. Emoji occupies 2 columns in a terminal - not just 2 Java chars.
Both aspects need to be handled.

### Solution 1: Flags isHighSurrogate & isLowSurrogate in Cell

Implementation:
- Add isHighSurrogate and isLowSurrogate flags to Cell
- Iterate over code points instead of toCharArray()
- Extract high and low surrogate and save each to a separate Cell with the appropriate flag
- When reading, check flags and combine neighboring cells to reconstruct the emoji

Pros:
- Base type of Cell stays as Character - minimal change to existing code

Cons:
- getCharacter() returns only half of an emoji - not a valid character on its own
- Every read operation requires flag checks and neighboring cell lookup
- All future methods reading cell content must be aware of this logic

### Solution 2: Storing String in each Cell

Implementation:
- Change Character -> String
- Iterate over code points instead of toCharArray()
- Convert code point to String via new String(Character.toChars(codePoint)) and store in Cell
- Mark the next cell as blocked (isBlocked = true) — emoji occupies 2 columns so the neighboring cell must not be written to
- getCharacter() returns String

Pros:
- Emoji is stored and read as a complete, valid character out of the box
- No additional logic needed at read time
- Clean and idiomatic Java

Cons:
- Base type change in Cell required
- getCharacter() returns String instead of Character — minor API change

### Solution 3: Storing int (code point) in Cell

Implementation:
- Change Character -> int in Cell
- Iterate over code points and store directly as int
- Convert to String on read via new String(Character.toChars(codePoint))
- Mark next cell as blocked

Pros:
- int is a primitive — less memory overhead than a String object
- One value represents every character including emoji, no surrogate pair needed

Cons:
- Conversion from int to String required on every read
- Base type change required just like Solution 2
- Since String is needed anyway for output, Solution 2 is simpler

### Final Decision

There is no perfect solution. Storing a single Character per Cell is no longer viable with emoji support.
Despite the requirement stating "Get character at position", storing emoji in a char or Character is physically
impossible - a code point above 0xFFFF does not fit in 16 bits.

Solution 2 (String) was chosen because it is the simplest and most correct approach for Java.
The emoji is stored as a complete, valid String and can be read directly without any reconstruction logic.
The isBlocked flag on the neighboring cell tells the renderer that the column is occupied by the right half of a wide character.

---

## Potential Improvements

- Width resize with reflow - the current implementation cuts content when decreasing width. A better approach would be to rebuild screen and redistribute content or something like that
- Wide char integrity on width decrease - if an emoji sits at the last visible column after decreasing width, the primary cell remains on screen while its blocked cell is cut off, or vice versa. This needs to be handled

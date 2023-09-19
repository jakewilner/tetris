Tetris README:

Overview:

App:
The top-level class, starts the game, sets up frame, etc.

PaneOrganizer:
Sets up the panes, buttons and image views associated with the game. Assists in the reset and contains an
instance of class game.

Game:
Is associated with the gamePane, infoPane and score and high score labels. Sets up the timeline and keyframe,
and controls most of the game’s logic, calling methods on the piece in pieceList, manipulating the colors of the board,
which is a 2D array of rectangles. This class updates the information pane, and generates pieces.

Piece:
Piece is associated with the board and pieceList. It controls the movement of the pieces, which change colors
on the board.
It checks for collisions and clears rows. It also controls the incrementing keyframe duration.
Piece has a number of child classes, that each provide the properties, such as the orientation and color of the squares
that differentiate pieces.

Design Choices:
The pieces are not rendered elements, rather they represent color changes in the array of rectangles: board.
They use these colors to check for collisions. The pieces themselves store coordinates, representing the squares that
they are currently setting the color of.
The current piece is stored in an ArrayList of pieces, that only ever contains 1 or 0 pieces. By using an arrayList,
it is very easy to generate the next pieces, using an ArrayList of numbers to provide the sequence. Numbers represent
the individual pieces, and are passed into the generation algorithm, where it converts those numbers into pieces.
If switching to incrementing mode during a round, it will go to the appropriate difficulty for the rows you have
already cleared.

Debugging Collaborators:
N/A

Known Bugs:
N/A

Bells and Whistles:
Upcoming blocks, start screen, bag random, hold, incrementing difficulty mode, score multipliers, levels
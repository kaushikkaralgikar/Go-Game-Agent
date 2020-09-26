import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;

public class my_player {

	private static final String INPUT = "input.txt";
	private static final String OUTPUT = "output.txt";
	private static final String HELPER = "helper.txt";
	private static final int BLACK = 1;
	private static final int WHITE = 2;
	private static final int EMPTY = 0;
	private static final int INT_MIN = -99999999;
	private static final int INT_MAX = 99999999;
	private static final String PASS = "PASS";
	private static final String PLAY = "PLAY";

	static class Position {
		int x;
		int y;
		String colour;
		double utilityValue;
		String passOrPlay;
		ArrayList<String> liberty = new ArrayList<String>();

		public Position(int x, int y, String colour, double utilityValue) {
			this.x = x;
			this.y = y;
			this.colour = colour;
			this.utilityValue = utilityValue;
		}

		public Position() {
			// TODO Auto-generated constructor stub
		}

		public Position(int x, int y, double moveScore) {
			this.x = x;
			this.y = y;
			this.utilityValue = moveScore;
		}

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getX() {
			return x;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getY() {
			return y;
		}

		public void setColour(String colour) {
			this.colour = colour;
		}

		public String getColour() {
			return colour;
		}

		public void setUtilityValue(double utilityValue) {
			this.utilityValue = utilityValue;
		}

		public double getUtilityValue() {
			return utilityValue;
		}

		public void setPassOrPlay(String passOrPlay) {
			this.passOrPlay = passOrPlay;
		}

		public String getPassOrPlay() {
			return passOrPlay;
		}

		public void setLiberty(ArrayList<String> liberty) {
			this.liberty = liberty;
		}

		public ArrayList<String> getLiberty() {
			return liberty;
		}
	}

	static class utilityValueComparator implements Comparator<Position> {

		@Override
		public int compare(Position arg0, Position arg1) {
			if (arg0.getUtilityValue() < arg1.getUtilityValue()) {
				return 1;
			} else if (arg0.getUtilityValue() > arg1.getUtilityValue()) {
				return -1;
			}
			return 0;
		}

	}

	public static void main(String args[]) {
		File input_file = new File(INPUT);
		File helper_file = new File(HELPER);
		try {
			BufferedReader br = new BufferedReader(new FileReader(input_file));
			BufferedReader hbr = new BufferedReader(new FileReader(helper_file));
			String text = null;
			String htext = null;
			int playingColour = 0;
			int noOfMovesLeft = 0;
			if ((htext = hbr.readLine()) != null) {
				noOfMovesLeft = Integer.parseInt(htext);
			}
			int oppositeColour = 0;
			if ((text = br.readLine()) != null) {
				playingColour = Integer.parseInt(text);
			}
			if (playingColour == BLACK) {
				oppositeColour = WHITE;
			} else {
				oppositeColour = BLACK;
			}
			if (noOfMovesLeft == 0|noOfMovesLeft == -1) {
				if (playingColour == BLACK) {
					noOfMovesLeft = 25;
				} else {
					noOfMovesLeft = 24;
				}
			}
			double currentPlayerCount = 0;
			double currentEmptyCount = 0;
			if (playingColour == WHITE) {
				currentPlayerCount = 2.5;
			}
			int[][] previousState = new int[5][5];
			for (int i = 0; i < 5; i++) {
				if ((text = br.readLine()) != null) {
					String textArray[] = text.split("");
					for (int j = 0; j < 5; j++) {
						previousState[i][j] = Integer.parseInt(textArray[j]);
					}
				}
			}
			int[][] currentState = new int[5][5];
			int[][] basicUtility = new int[5][5];
			for (int i = 0; i < 5; i++) {
				if ((text = br.readLine()) != null) {
					String textArray[] = text.split("");
					for (int j = 0; j < 5; j++) {
						if (Integer.parseInt(textArray[j]) == BLACK) {
							if (playingColour == BLACK) {
								currentPlayerCount += 1;
							}
							basicUtility[i][j] = INT_MIN;
						} else if (Integer.parseInt(textArray[j]) == WHITE) {
							if (playingColour == WHITE) {
								currentPlayerCount += 1;
							}
							basicUtility[i][j] = INT_MIN;
						} else {
							currentEmptyCount += 1;
							basicUtility[i][j] = 0;
						}
						currentState[i][j] = Integer.parseInt(textArray[j]);
					}
				}
			}
			// KO Check
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					if (currentState[i][j] == EMPTY) {
						currentState[i][j] = playingColour;
						if (Arrays.deepEquals(previousState, currentState)) {
							basicUtility[i][j] = INT_MIN;
						}
						currentState[i][j] = EMPTY;
					}
				}
			}
			Position newMove = new Position();
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					if (currentState[i][j] - previousState[i][j] == oppositeColour) {
						newMove = new Position(i, j);
					}
				}
			}
			ArrayList<Position> newMoveNeighBours = detectNeighbours(newMove, currentState, playingColour,
					oppositeColour);
			Position move = new Position();
			if (currentEmptyCount + (2 * currentPlayerCount) - 25 > 4) {
				move.setPassOrPlay(PASS);
			} else {
				move = calculateNextMove(playingColour, oppositeColour, currentState, basicUtility, currentPlayerCount,
						currentEmptyCount, noOfMovesLeft, newMoveNeighBours);
			}

			File outFile = new File(OUTPUT);
			FileWriter fileWriter = new FileWriter(outFile);
			File helperFile = new File(HELPER);
			FileWriter helperFileWriter = new FileWriter(helperFile);
			helperFileWriter.write(Integer.toString(noOfMovesLeft - 2));

			if (move.getPassOrPlay().equals(PASS)) {
				fileWriter.write(PASS);
			} else {
				fileWriter.write(move.getX() + "," + move.getY());
			}
			br.close();
			hbr.close();
			fileWriter.close();
			helperFileWriter.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Position calculateNextMove(int playingColour, int oppositeColour, int[][] currentState,
			int[][] basicUtility, double currentPlayerCount, double currentEmptyCount, int noOfMovesLeft,
			ArrayList<Position> newMoveNeighBours) {
		int[][] staticCurrentState = arrayCopy(currentState);
		PriorityQueue<Position> initialQueue = new PriorityQueue<Position>(25, new utilityValueComparator());
		double maxDepth = 0;
		if (noOfMovesLeft <= 4) {
			maxDepth = 2;
		} else {
			maxDepth = 3;
		}
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (currentState[i][j] == EMPTY && basicUtility[i][j] != INT_MIN) {
					Position pos = new Position(i, j);
					initialQueue.add(pos);
				}
			}
		}

		PriorityQueue<Position> queue = new PriorityQueue<Position>(25, new utilityValueComparator());
		while (!initialQueue.isEmpty()) {
			Position pos = initialQueue.poll();
			int i = pos.getX();
			int j = pos.getY();
			if (currentState[i][j] == EMPTY && basicUtility[i][j] != INT_MIN) {
				currentState[i][j] = playingColour;
				double moveScore = minimax(currentState, basicUtility, staticCurrentState, playingColour,
						oppositeColour, currentPlayerCount + 1, currentEmptyCount - 1, 0, maxDepth, false, i, j,
						INT_MIN, INT_MAX);
				currentState = arrayCopy(staticCurrentState);
				Position p = new Position(i, j, moveScore);
				queue.add(p);
			}
		}

		Position move = new Position();
		PriorityQueue<Position> copy = new PriorityQueue<Position>(queue);
		PriorityQueue<Position> finalQueue = new PriorityQueue<Position>(25, new utilityValueComparator());
		boolean noScore = checkNoScore(copy);
		if (noScore) {
			while (!queue.isEmpty()) {
				Position pos = queue.poll();
				int i = pos.getX();
				int j = pos.getY();
				double moveScore = pos.getUtilityValue();
				for (int m = 0; m < newMoveNeighBours.size(); m++) {
					if (i == newMoveNeighBours.get(m).getX() && j == newMoveNeighBours.get(m).getY()) {
						moveScore += 13;
					}
				}
				ArrayList<Position> curNeighbours = detectNeighbours(pos, currentState, playingColour, oppositeColour);
				int neighbourOppositeCount = 0;
				int neighbourAllyCount = 0;
				int neighbourEmptyCount = 0;
				for (int z = 0; z < curNeighbours.size(); z++) {
					if (currentState[curNeighbours.get(z).getX()][curNeighbours.get(z).getY()] == playingColour) {
						neighbourAllyCount += 1;
					} else if (currentState[curNeighbours.get(z).getX()][curNeighbours.get(z)
							.getY()] == oppositeColour) {
						neighbourOppositeCount += 1;
					} else {
						neighbourEmptyCount += 1;
					}
				}
				if (moveScore != INT_MIN) {
					if (neighbourOppositeCount == curNeighbours.size() - 1) {
						moveScore -= neighbourOppositeCount;
					} else if (neighbourAllyCount == 0) {
						moveScore -= neighbourOppositeCount;
					} else if (neighbourOppositeCount == 0) {
						moveScore += neighbourAllyCount;
					}

					if ((i == 2 && j == 2)) {
						moveScore += 14;
					}
					if ((i == 1 && j == 2) | (i == 2 && j == 1) | (i == 2 && j == 3) | (i == 2 && j == 4)) {
						moveScore += 12;
					} else if ((i == 0 && j == 2) | (i == 1 && j == 1) | (i == 1 && j == 3) | (i == 2 && j == 0)
							| (i == 3 && j == 1) | (i == 3 && j == 3) | (i == 4 && j == 2)) {
						moveScore += 10;
					}
				}
				pos.setUtilityValue(moveScore);
				finalQueue.add(pos);
			}
			if (!finalQueue.isEmpty()) {
				move = finalQueue.peek();
				if (move.getUtilityValue() > INT_MIN) {
					move.setPassOrPlay(PLAY);
				} else {
					move.setPassOrPlay(PASS);
				}
			} else {
				move.setPassOrPlay(PASS);
			}
			return move;
		} else {

			if (!queue.isEmpty()) {
				move = queue.peek();
				if (move.getUtilityValue() > INT_MIN) {
					move.setPassOrPlay(PLAY);
				} else {
					move.setPassOrPlay(PASS);
				}
			} else {
				move.setPassOrPlay(PASS);
			}
			return move;
		}
	}

	private static boolean checkNoScore(PriorityQueue<Position> queue) {
		ArrayList<Position> array = new ArrayList<Position>();
		for (int i = 0; i < queue.size(); i++) {
			array.add(queue.poll());
		}
		Position first = array.get(0);
		for (int i = 1; i < array.size(); i++) {
			if (array.get(i).getUtilityValue() != first.getUtilityValue()) {
				return false;
			}
		}
		return true;
	}

	private static int[][] arrayCopy(int[][] src) {
		int[][] dst = new int[src.length][];
		for (int i = 0; i < src.length; i++) {
			dst[i] = Arrays.copyOf(src[i], src[i].length);
		}
		return dst;
	}

	private static double minimax(int[][] currentState, int[][] basicUtility, int[][] staticCurrentState,
			int playingColour, int oppositeColour, double currentPlayerCount, double currentEmptyCount, int depth,
			double maxDepth, boolean isMax, int i, int j, double alpha, double beta) {
		double moveScore = 0;

		if (isMax) {
			int temp = playingColour;
			playingColour = oppositeColour;
			oppositeColour = temp;
		}
		int[][] liberty = calculateLiberty(currentState, playingColour, oppositeColour);
		if (depth == 0) {
			int suicideLiberty[][] = calculateLiberty(currentState, oppositeColour, playingColour);
			if (suicideLiberty[i][j] == 0) {
				moveScore = INT_MIN + depth;
			}
		}
		if (moveScore != 0) {
			return moveScore;
		}

		for (int k = 0; k < 5; k++) {
			for (int m = 0; m < 5; m++) {
				if (liberty[k][m] == 0) {
					if (isMax) {
						if (currentState[k][m] == oppositeColour) {
							currentPlayerCount -= 1;
						}
					} else {
						if (currentState[k][m] == playingColour) {
							currentPlayerCount -= 1;
						}
					}
					currentState[k][m] = EMPTY;
					currentEmptyCount += 1;
				}
			}
		}
		if (isMax) {
			int temp = playingColour;
			playingColour = oppositeColour;
			oppositeColour = temp;
		}

		if (depth == (maxDepth - 1)) {
			return (2 * currentPlayerCount) + currentEmptyCount - 25;
		} else {
			if (isMax) {
				double best = INT_MIN;
				int bflag = 0;
				int dflag = 0;
				for (int k = 0; k < 5; k++) {
					for (int m = 0; m < 5; m++) {
						if (currentState[k][m] == EMPTY && basicUtility[k][m] != INT_MIN) {
							int[][] staticCurrentState1 = arrayCopy(currentState);
							currentState[k][m] = playingColour;
							double val = minimax(currentState, basicUtility, staticCurrentState, playingColour,
									oppositeColour, currentPlayerCount + 1, currentEmptyCount - 1, depth + 1, maxDepth,
									!isMax, k, m, alpha, beta);
							best = Math.max(best, val);
							alpha = Math.max(alpha, best);
							currentState = arrayCopy(staticCurrentState1);
							if (beta <= alpha) {
								bflag = 1;
								break;
							}
							if (depth == maxDepth) {
								dflag = 1;
								break;
							}
						}
					}
					if (bflag == 1) {
						break;
					}
					if (dflag == 1) {
						break;
					}
				}
				return best;
			} else {
				double best = INT_MAX;
				int bflag = 0;
				int dflag = 0;
				for (int k = 0; k < 5; k++) {
					for (int m = 0; m < 5; m++) {
						if (currentState[k][m] == EMPTY && basicUtility[k][m] != INT_MIN) {
							int[][] staticCurrentState1 = arrayCopy(currentState);
							currentState[k][m] = oppositeColour;
							double val = minimax(currentState, basicUtility, staticCurrentState, playingColour,
									oppositeColour, currentPlayerCount, currentEmptyCount - 1, depth + 1, maxDepth,
									!isMax, k, m, alpha, beta);
							best = Math.min(best, val);
							beta = Math.min(beta, best);
							currentState = arrayCopy(staticCurrentState1);
							if (beta <= alpha) {
								bflag = 1;
								break;
							}
							if (depth == maxDepth) {
								dflag = 1;
								break;
							}
						}
					}
					if (bflag == 1) {
						break;
					}
					if (dflag == 1) {
						break;
					}
				}
				return best;
			}
		}
	}

	private static int[][] calculateLiberty(int[][] currentState, int playingColour, int oppositeColour) {
		int[][] liberty = new int[5][5];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				liberty[i][j] = -1;
			}
		}
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (currentState[i][j] == oppositeColour) {
					liberty[i][j] = setLiberty(currentState, playingColour, oppositeColour, i, j);
				}
			}
		}
		return liberty;
	}

	private static int setLiberty(int[][] currentState, int playingColour, int oppositeColour, int i, int j) {
		ArrayList<Position> allyMembers = allyDfs(currentState, playingColour, oppositeColour, i, j);
		if (allyMembers != null && !allyMembers.isEmpty()) {
			for (int k = 0; k < allyMembers.size(); k++) {
				ArrayList<Position> neighbours = detectNeighbours(allyMembers.get(k), currentState, i, j);
				for (int m = 0; m < neighbours.size(); m++) {
					if (currentState[neighbours.get(m).getX()][neighbours.get(m).getY()] == EMPTY) {
						return 1;
					}
				}
			}
		}
		return 0;
	}

	private static ArrayList<Position> allyDfs(int[][] currentState, int playingColour, int oppositeColour, int i,
			int j) {
		int aflag = 0;
		int sflag = 0;
		ArrayList<Position> allyMembers = new ArrayList<Position>();
		Stack<Position> stack = new Stack<Position>();
		stack.add(new Position(i, j));
		while (!stack.empty()) {
			Position pos = stack.pop();
			allyMembers.add(pos);
			ArrayList<Position> neighbourAllies = detectNeighbourAllies(pos, currentState, playingColour,
					oppositeColour);
			if (neighbourAllies != null && !neighbourAllies.isEmpty()) {
				for (int k = 0; k < neighbourAllies.size(); k++) {
					for (int m = 0; m < allyMembers.size(); m++) {
						if ((neighbourAllies.get(k).getX() == allyMembers.get(m).getX()
								&& neighbourAllies.get(k).getY() == allyMembers.get(m).getY())) {
							aflag = 1;
						}
					}
					if (aflag == 0) {
						for (int z = 0; z < stack.size(); z++) {
							if ((neighbourAllies.get(k).getX() == stack.get(z).getX()
									&& neighbourAllies.get(k).getY() == stack.get(z).getY())) {
								sflag = 1;
							}
						}
						if (sflag == 0) {
							stack.add(neighbourAllies.get(k));
						} else {
							sflag = 0;
						}
					} else {
						aflag = 0;
					}
				}
			}
		}
		return allyMembers;
	}

	private static ArrayList<Position> detectNeighbourAllies(Position pos, int[][] currentState, int playingColour,
			int oppositeColour) {
		ArrayList<Position> groupAllies = new ArrayList<Position>();
		ArrayList<Position> neighbours = detectNeighbours(pos, currentState, playingColour, oppositeColour);
		while (!neighbours.isEmpty()) {
			Position curNeighbour = neighbours.get(0);
			neighbours.remove(0);
			if (currentState[curNeighbour.getX()][curNeighbour.getY()] == currentState[pos.getX()][pos.getY()]) {
				groupAllies.add(curNeighbour);
			}
		}
		return groupAllies;
	}

	private static ArrayList<Position> detectNeighbours(Position pos, int[][] currentState, int playingColour,
			int oppositeColour) {
		ArrayList<Position> neighbours = new ArrayList<Position>();
		int x = pos.getX();
		int y = pos.getY();
		if (x > 0) {
			neighbours.add(new Position(x - 1, y));
		}
		if (x < 4) {
			neighbours.add(new Position(x + 1, y));
		}
		if (y > 0) {
			neighbours.add(new Position(x, y - 1));
		}
		if (y < 4) {
			neighbours.add(new Position(x, y + 1));
		}
		return neighbours;
	}
}

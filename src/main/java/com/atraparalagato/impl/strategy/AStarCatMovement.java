package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementación esqueleto de estrategia de movimiento usando algoritmo A*.
 */
public class AStarCatMovement extends CatMovementStrategy<HexPosition> {
	
	public AStarCatMovement(GameBoard<HexPosition> board) {
		super(board);
	}
	
	@Override
	protected List<HexPosition> getPossibleMoves(HexPosition currentPosition) {
		return board.getAdjacentPositions(currentPosition).stream()
			.filter(Predicate.not(board::isBlocked))
			.collect(Collectors.toList());
	}
	
	@Override
	protected Optional<HexPosition> selectBestMove(List<HexPosition> possibleMoves, 
													HexPosition currentPosition, 
													HexPosition targetPosition) {
		if (possibleMoves.isEmpty()) {
			return Optional.empty();
		}

		Function<HexPosition, Double> heuristicFunction = getHeuristicFunction(targetPosition);

		return possibleMoves.stream()
			.min(Comparator.comparing(heuristicFunction::apply));
	}
	
	public double getDistanceToBorder(HexPosition position) {
        return Math.min(
			Math.min(board.getSize() - Math.abs(position.getQ()),
				board.getSize() - Math.abs(position.getR())),
			Math.abs(position.getS()));
    }
	
	@Override
	protected Function<HexPosition, Double> getHeuristicFunction(HexPosition targetPosition) {
		return (_position) -> {
			Set<HexPosition> blockedPositions = board.getBlockedPositions();
			double blockedPositionsWeight = blockedPositions.stream()
				.map((__position) -> __position.distanceTo(_position))
				.reduce(0.0, Double::sum);
			
			return Math.max(0, getDistanceToBorder(_position) - (blockedPositionsWeight / (2 * blockedPositions.size() * board.getSize())));
		};
	}
	
	@Override
	protected Predicate<HexPosition> getGoalPredicate() {
		return (_position) -> {
			return _position.isAtBorder(board.getSize());
		};
	}
	
	@Override
	protected double getMoveCost(HexPosition from, HexPosition to) {
		return 1.0;
	}
	
	@Override
	public boolean hasPathToGoal(HexPosition currentPosition) {
		return !getFullPath(currentPosition, null).isEmpty();
	}
	
	private List<HexPosition> reconstructPath(AStarNode goalNode) {
		List<HexPosition> path = new LinkedList<>();
		AStarNode currentNode = goalNode;

		while (currentNode.parent != null) {
			path.add(currentNode.position);

			currentNode = currentNode.parent;
		}

		Collections.reverse(path);
		return path;
	}
	
	@Override
	public List<HexPosition> getFullPath(HexPosition currentPosition, HexPosition targetPosition) {
		PriorityQueue<AStarNode> openSet = new PriorityQueue<>(Comparator.comparing((_Node) -> _Node.fScore));
		Set<HexPosition> closedSet = new HashSet<>();
		Map<HexPosition, AStarNode> allNodes = new HashMap<>();

		Function<HexPosition, Double> heuristicFunction = getHeuristicFunction(targetPosition);

		AStarNode startNode = new AStarNode(currentPosition, 0, heuristicFunction.apply(currentPosition), null);
		openSet.offer(startNode);
		allNodes.put(currentPosition, startNode);

		do {
			AStarNode currentNode = openSet.poll();

			if (currentNode.position.equals(targetPosition) || getGoalPredicate().test(currentNode.position)) {
				return reconstructPath(currentNode);
			}

			closedSet.add(currentNode.position);

			for (HexPosition neighbourPosition : getPossibleMoves(currentNode.position)) {
				if (closedSet.contains(neighbourPosition)) continue;

				double neighbourGScore = currentNode.gScore + getMoveCost(currentNode.position, neighbourPosition);

				if (!allNodes.containsKey(neighbourPosition) || neighbourGScore < allNodes.get(neighbourPosition).gScore) {
					double neighbourFScore = neighbourGScore + heuristicFunction.apply(neighbourPosition);

					AStarNode neighbourNode = new AStarNode(neighbourPosition, neighbourGScore, neighbourFScore, currentNode);

					allNodes.put(neighbourPosition, neighbourNode);
					openSet.offer(neighbourNode);
				}
			}
		} while (!openSet.isEmpty());

		return Collections.emptyList();
	}

	/**
	 * Método público para obtener el siguiente movimiento óptimo del gato.
	 */
	public Optional<HexPosition> getNextMove(HexPosition currentPosition) {
		List<HexPosition> path = getFullPath(currentPosition, null);
		if (path == null || path.isEmpty()) return Optional.empty();
		return Optional.of(path.get(0));
	}
	
	private static class AStarNode {
		public final HexPosition position;
		public final double gScore; // Costo desde inicio
		public final double fScore; // gScore + heurística
		public final AStarNode parent;
		
		public AStarNode(HexPosition position, double gScore, double fScore, AStarNode parent) {
			this.position = position;
			this.gScore = gScore;
			this.fScore = fScore;
			this.parent = parent;
		}
	}
	
	@Override
	protected void beforeMovementCalculation(HexPosition currentPosition) {
		super.beforeMovementCalculation(currentPosition);
	}
	
	@Override
	protected void afterMovementCalculation(Optional<HexPosition> selectedMove) {
		super.afterMovementCalculation(selectedMove);
	}
}

package com.cgvsu.protocurvefxapp;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ProtoCurveController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    private List<Point2D> points = new ArrayList<>();

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        canvas.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY -> handlePrimaryClick(event);
            }
        });
        drawBackgroundSquare();
        drawLeastSquaresLine();
    }

    private void handlePrimaryClick(MouseEvent event) {
        final Point2D clickPoint = new Point2D(event.getX(), event.getY());
        if (isPointInsideSquare(clickPoint)) points.add(clickPoint);

        if (points.size() > 1) {
            drawLeastSquaresLine();
            drawPoints();
        } else {
            drawPoint(clickPoint);
        }
    }

    private void drawLeastSquaresLine() {
        double sumX = 0;
        double sumY = 0;

        for (Point2D point : points) {
            sumX += point.getX();
            sumY += point.getY();
        }

        double centerX = sumX / points.size();
        double centerY = sumY / points.size();

        double sumNumer = 0;
        double sumDenom = 0;

        for (Point2D point : points) {
            sumNumer += (point.getX() - centerX) * (point.getY() - centerY);
            sumDenom += Math.pow((point.getX() - centerX), 2);
        }

        double slope = sumNumer / sumDenom;
        double intercept = centerY - slope * centerX;

        double startX = 0;
        double startY = startX * slope + intercept;
        double endX = canvas.getWidth();
        double endY = endX * slope + intercept;

        double squareSize = 500;
        double squareMinX = (canvas.getWidth() - squareSize) / 2;
        double squareMaxX = squareMinX + squareSize;
        double squareMinY = (canvas.getHeight() - squareSize) / 2 - 45;
        double squareMaxY = squareMinY + squareSize;

        if (startX < squareMinX) {
            startX = squareMinX;
            startY = startX * slope + intercept;
        } else if (startX > squareMaxX) {
            startX = squareMaxX;
            startY = startX * slope + intercept;
        }

        if (startY < squareMinY) {
            startY = squareMinY;
            startX = (startY - intercept) / slope;
        } else if (startY > squareMaxY) {
            startY = squareMaxY;
            startX = (startY - intercept) / slope;
        }

        if (endX < squareMinX) {
            endX = squareMinX;
            endY = endX * slope + intercept;
        } else if (endX > squareMaxX) {
            endX = squareMaxX;
            endY = endX * slope + intercept;
        }

        if (endY < squareMinY) {
            endY = squareMinY;
            endX = (endY - intercept) / slope;
        } else if (endY > squareMaxY) {
            endY = squareMaxY;
            endX = (endY - intercept) / slope;
        }

        if (points.size() == 0) {
            startY = canvas.getHeight() / 2 - 45;
            endY = startY;
        }
        drawLine(startX, startY, endX, endY);

    }


    private void drawBackgroundSquare() {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        double squareSize = 500;
        double centerX = (canvas.getWidth() - squareSize) / 2;
        double centerY = (canvas.getHeight() - squareSize) / 2 - 45;

        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeRect(centerX, centerY, squareSize, squareSize);

        double interval = squareSize / 8.0;

        for (int i = -4; i <= 4; i++) {
            double x = centerX + interval * (i + 4);
            graphicsContext.strokeLine(x, centerY + squareSize, x, centerY + squareSize + 5);
            graphicsContext.fillText(String.valueOf(i), x - 3, centerY + squareSize + 20);
        }

        for (int i = 4; i >= -4; i--) {
            double y = centerY + interval * Math.abs(i - 4);
            graphicsContext.strokeLine(centerX - 5, y, centerX, y);
            graphicsContext.fillText(String.valueOf(i), centerX - 25, y + 4);
        }
    }

    private boolean isPointInsideSquare(Point2D point) {
        double squareSize = 500; // Задаем размер квадрата
        double centerX = (canvas.getWidth() - squareSize) / 2;
        double centerY = (canvas.getHeight() - squareSize) / 2 - 45;

        return point.getX() >= centerX && point.getX() <= centerX + squareSize &&
                point.getY() >= centerY && point.getY() <= centerY + squareSize;
    }

    private void drawPoints() {
        drawBackgroundSquare();

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        final int POINT_RADIUS = 3;

        for (Point2D point : points) {
            if (isPointInsideSquare(point)) {
                graphicsContext.setFill(Color.BLACK);
                graphicsContext.fillOval(
                        point.getX() - POINT_RADIUS, point.getY() - POINT_RADIUS,
                        2 * POINT_RADIUS, 2 * POINT_RADIUS);
            }
        }
    }

    private void drawPoint(Point2D point) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        final int POINT_RADIUS = 3;
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillOval(
                point.getX() - POINT_RADIUS, point.getY() - POINT_RADIUS,
                2 * POINT_RADIUS, 2 * POINT_RADIUS);
    }

    private void drawLine(double startX, double startY, double endX, double endY) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawPoints();
        graphicsContext.setStroke(Color.RED);
        graphicsContext.strokeLine(startX, startY, endX, endY);
    }
}



package task02;

public record Point2D(double x, double y) {
    public double distanceTo(Point2D other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
    
    public Point2D rotate(final double angle) {
        double radians = Math.toRadians(angle);
        double newX = this.x * Math.cos(radians) - this.y * Math.sin(radians);
        double newY = this.x * Math.sin(radians) + this.y * Math.cos(radians);
        return new Point2D(newX, newY);
    }
    
    public Point2D translate(final double dx, final double dy) {
        return new Point2D(this.x + dx, this.y() + dy);
    }
}

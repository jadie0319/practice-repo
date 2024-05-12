package jadie.ticketorder.domain;

import java.util.List;

public class OrderLines {
    private List<OrderLine> lines;

    public OrderLines(List<OrderLine> lines) {
        this.lines = lines;
    }

    public void changeOrderLines(List<OrderLine> newLines) {
        this.lines = newLines;
    }

    public int getTotalAmount() {
        return 0;
    }
}

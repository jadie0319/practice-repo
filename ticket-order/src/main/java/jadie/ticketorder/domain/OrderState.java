package jadie.ticketorder.domain;

public enum OrderState {
    PAYMENT_WAITING {
        public boolean isShippingChangeable() {
            return false;
        }
    },

    PREPARING {
        public boolean isShippingChangeable() {
            return true;
        }
    },
    SHIPPED, DELIVERING, DELIVERY_COMPLETED
    ;

    public boolean isShippingChangeable() {
        return false;
    }
}

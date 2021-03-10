package io.helidon.examples.quickstart.se;




public class CreateReservation {
    private String OrganizationCode;
    private String ItemNumber;
    private String SubinventoryCode;
    private int ReservationQuantity;

    public CreateReservation(String organizationCode, String itemNumber, String subinventoryCode, int reservationQuantity) {
        OrganizationCode = organizationCode;
        ItemNumber = itemNumber;
        SubinventoryCode = subinventoryCode;
        ReservationQuantity = reservationQuantity;

    }



    public String getOrganizationCode() {
        return OrganizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        OrganizationCode = organizationCode;
    }

    public String getItemNumber() {
        return ItemNumber;
    }

    public void setItemNumber(String itemNumber) {
        ItemNumber = itemNumber;
    }

    public String getSubinventoryCode() {
        return SubinventoryCode;
    }

    public void setSubinventoryCode(String subinventoryCode) {
        SubinventoryCode = subinventoryCode;
    }

    public int getReservationQuantity() {
        return ReservationQuantity;
    }

    public void setReservationQuantity(int reservationQuantity) {
        ReservationQuantity = reservationQuantity;
    }
}

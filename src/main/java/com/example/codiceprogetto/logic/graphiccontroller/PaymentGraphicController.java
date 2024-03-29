package com.example.codiceprogetto.logic.graphiccontroller;

import com.example.codiceprogetto.logic.appcontroller.PaymentApplicativeController;
import com.example.codiceprogetto.logic.bean.OrderBean;
import com.example.codiceprogetto.logic.bean.PaymentBean;
import com.example.codiceprogetto.logic.enumeration.PaymentType;
import com.example.codiceprogetto.logic.exception.DAOException;
import com.example.codiceprogetto.logic.exception.EmptyInputException;
import com.example.codiceprogetto.logic.utils.Utilities;
import com.example.codiceprogetto.logic.utils.SessionUser;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaymentGraphicController extends Utilities {
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField expirationField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField zipField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField cvvField;
    @FXML
    private Label totalAmount;
    @FXML
    private CheckBox paymetCheckbox;
    PaymentApplicativeController toPay;
    int count = 1;

    @FXML
    void initialize() {
        OrderBean orderBean = new OrderBean();
        PaymentApplicativeController cOut = new PaymentApplicativeController();
        toPay = new PaymentApplicativeController();

        try {
            orderBean = cOut.fetchTotal(SessionUser.getInstance().getThisUser().getEmail(), orderBean);
        } catch(SQLException e) {
            Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
        }

        totalAmount.setText(round(orderBean.getFinalTotal(), 2) + "€");
    }

    public void homeGUI() {
        try {
            toPay.deleteOrder(SessionUser.getInstance().getThisUser().getEmail());
            navigateTo(HOME);
        } catch (DAOException e) {
            Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
        }
    }

    public void pay(MouseEvent mouseEvent) {
        String toDisplay = "There isn't any memorized payment method!";
        PaymentType payment = PaymentType.PAYPAL;

        if (paymetCheckbox.isSelected() && checkPayment()) {
            alert(toDisplay);
            return;
        }

        if(count % 2 == 0) {
            alert("Payment rejected, order deleted!");
            homeGUI();
        }

        Node source = (Node) mouseEvent.getSource();
        if (source instanceof Button)
            payment = PaymentType.fromString(source.getId());

        PaymentBean paymentBean;

        if (payment == PaymentType.CARD && !paymetCheckbox.isSelected()) {
            paymentBean = new PaymentBean(nameField.getText(),
                    lastNameField.getText(),
                    expirationField.getText(),
                    cardNumberField.getText(),
                    cvvField.getText(),
                    zipField.getText());

            try {
                if (paymentBean.getName().isEmpty() ||
                        paymentBean.getLastName().isEmpty() ||
                        paymentBean.getExpiration().isEmpty() ||
                        paymentBean.getCardNumber().isEmpty() ||
                        paymentBean.getCvv().isEmpty() ||
                        paymentBean.getZipCode().isEmpty())
                    throw new EmptyInputException("There are some empty fields!");

                if (checkPayment() && displayConfirmBox("Do you want to save your payment method?", "yes", "no")) {
                    toPay.insertPaymentMethod(paymentBean, SessionUser.getInstance().getThisUser().getEmail());
                }
            } catch (EmptyInputException e) {
                count++;
                alert(e.getMessage());
                return;
            }
        }

        try {
            assert payment != null;
            toPay.createTransaction(SessionUser.getInstance().getThisUser().getEmail(), payment);
            navigateTo(PAYSUM);
        } catch (SQLException e) {
            count++;
            Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
        }
    }


    private boolean checkPayment() {
        boolean res = false;

        try {
            res = toPay.checkCustomerPaymentMethod(SessionUser.getInstance().getThisUser().getEmail());
        } catch (SQLException e) {
            Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
        } catch (DAOException e) {
            alert(e.getMessage());
        }

        return !res;
    }
}

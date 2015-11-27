package user.ihm.popup;

import user.User;
import user.ihm.UserIHM;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PopupTweet extends JDialog {

	private JTextField textFieldTweet;
	private UserIHM ihm;

	public PopupTweet(UserIHM i) {
		ihm = i;
		setTitle("Tweet");
		setSize(450,145);
		setLocationRelativeTo(null);
		setResizable(false);
		setModal (true);
		setAlwaysOnTop (true);
		setModalityType (ModalityType.APPLICATION_MODAL);

		getContentPane().setLayout(null);

		JLabel lblRetirer = new JLabel("Entrez votre message :");
		lblRetirer.setBounds(10, 11, 324, 33);
		getContentPane().add(lblRetirer);

		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ihm.actionSendTweet(textFieldTweet.getText());
				dispose();
			}
		});
		btnOk.setBounds(130, 82, 89, 23);
		getContentPane().add(btnOk);

		textFieldTweet = new JTextField();
		textFieldTweet.setBounds(41, 43, 363, 20);
		getContentPane().add(textFieldTweet);
		textFieldTweet.setColumns(10);

		JButton btnAnnuler = new JButton("Annuler");
		btnAnnuler.setBounds(229, 82, 89, 23);
		btnAnnuler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		getContentPane().add(btnAnnuler);
		setVisible(true);
	}

}

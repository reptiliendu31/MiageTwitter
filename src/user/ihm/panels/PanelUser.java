package user.ihm.panels;

import bdd.objetBdd.MessageBDD;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.AbstractTableModel;

import bdd.objetBdd.UserBDD;
import user.ihm.UserIHM;
import user.ihm.enums.Etat;
import user.ihm.enums.Lieu;
import user.ihm.popup.PopupErreur;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.LayoutStyle.ComponentPlacement;

public class PanelUser extends JPanel {
	private JPanel courant = this;
	private JPanel panelBox, panelCenter; 
	private BorderLayout layout ;
	private JTable tableauFlux;
	private ArrayList<MessageBDD> messagesFlux;
	private JLabel lblMurMessages ;
	private JPanel panelNorth;
	private UserIHM ihm;
	private TableFlux donneesTableFlux, donneesTableLoc;
	private final JButton btnChercher = new JButton("G�rer abonn�s");
	private JButton btnGrerMesMessages;
	private JLabel lblLocalisation;
	private JTextField textFieldLoc;
	private JComboBox comboBoxLieu;
	private JButton btnDconnexion;
	private UserBDD user;



	public PanelUser(UserIHM g, UserBDD user, ArrayList<Object[]> fl) {
		messagesFlux = new ArrayList<>() ;
		ihm = g;
		panelBox= new JPanel();
		panelCenter = new JPanel();
		layout = new BorderLayout();
		
		// Cr�ation du layout
		this.setLayout(layout);

		this.add(panelBox,BorderLayout.NORTH);
		lblMurMessages = new JLabel("Mon mur :\r\n");
		lblMurMessages.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblMurMessages.setHorizontalAlignment(SwingConstants.LEFT);

		JLabel lblIdentifiant = new JLabel("Identifiant : " + user.getLogin() );

		JLabel lblCodeSecret = new JLabel("Nom : " + user.getFirstName() + " " + user.getName());

		lblLocalisation = new JLabel("Localisation :");

		JButton btnChanger = new JButton("Changer");
		btnChanger.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String loc = ((Lieu)comboBoxLieu.getSelectedItem()).toString();
				ihm.actionSendLoc(loc);
			}
		});
		comboBoxLieu = new JComboBox(Lieu.values());
		comboBoxLieu.setSelectedItem(Lieu.valueOf(user.getLocalisation()));
		GroupLayout gl_panelBox = new GroupLayout(panelBox);
		gl_panelBox.setHorizontalGroup(
				gl_panelBox.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelBox.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_panelBox.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panelBox.createSequentialGroup()
												.addComponent(lblIdentifiant)
												.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(lblCodeSecret)
												.addGap(409))
										.addGroup(gl_panelBox.createSequentialGroup()
												.addComponent(lblLocalisation)
												.addPreferredGap(ComponentPlacement.UNRELATED)
												.addComponent(comboBoxLieu, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(btnChanger)
												.addContainerGap(169, Short.MAX_VALUE))))
		);
		gl_panelBox.setVerticalGroup(
				gl_panelBox.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panelBox.createSequentialGroup()
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(gl_panelBox.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblIdentifiant)
										.addComponent(lblCodeSecret))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_panelBox.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblLocalisation)
										.addComponent(btnChanger)
										.addComponent(comboBoxLieu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addContainerGap())
		);
		panelBox.setLayout(gl_panelBox);

		panelBox.setLayout(gl_panelBox);


		panelNorth = new JPanel();
		add(panelNorth, BorderLayout.SOUTH);
		panelNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		btnChercher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ihm.changerPanel(Etat.Abonnes);
			}
		});
		panelNorth.add(btnChercher);

		btnGrerMesMessages = new JButton("G\u00E9rer mes messages");
		btnGrerMesMessages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ihm.changerPanel(Etat.Messages);
			}
		});
		panelNorth.add(btnGrerMesMessages);

		btnDconnexion = new JButton("D\u00E9connexion");
		btnDconnexion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ihm.actionDisconnect();
				ihm.changerPanel(Etat.Menu);
			}
		});
		panelNorth.add(btnDconnexion);
		
		/* cr?ation du tableau  */
		donneesTableFlux = new TableFlux(fl);
		donneesTableLoc = new TableFlux(new ArrayList<Object[]>());

		add(panelCenter, BorderLayout.CENTER);
		tableauFlux = new JTable(donneesTableFlux);
		tableauFlux.setAutoCreateRowSorter(true);

		tableauFlux.setToolTipText("");
		ListSelectionModel listSelectionModel = tableauFlux.getSelectionModel();


		JTable tableauLoc = new JTable(donneesTableLoc);
		tableauLoc.setAutoCreateRowSorter(true);

		tableauLoc.setToolTipText("");
		JScrollPane scrollPane2 = new JScrollPane(tableauFlux);
		JScrollPane scrollPane = new JScrollPane(tableauLoc);

		JLabel lblMessagesDesAbonns = new JLabel("Messages des abonn\u00E9s :");

		JLabel lblMessagesDeLocalisation = new JLabel("Messages de localisation :");

		GroupLayout gl_panelCenter = new GroupLayout(panelCenter);
		gl_panelCenter.setHorizontalGroup(
				gl_panelCenter.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
						.addComponent(scrollPane2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
						.addGroup(gl_panelCenter.createSequentialGroup()
								.addContainerGap()
								.addComponent(lblMessagesDesAbonns)
								.addContainerGap(394, Short.MAX_VALUE))
						.addGroup(gl_panelCenter.createSequentialGroup()
								.addContainerGap()
								.addComponent(lblMessagesDeLocalisation)
								.addContainerGap(394, Short.MAX_VALUE))
		);
		gl_panelCenter.setVerticalGroup(
				gl_panelCenter.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panelCenter.createSequentialGroup()
								.addGap(10)
								.addComponent(lblMessagesDesAbonns)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(lblMessagesDeLocalisation)
								.addPreferredGap(ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE))
		);
		panelCenter.setLayout(gl_panelCenter);

		//listSelectionModel.addListSelectionListener(new ControleurTable());
	}
	
	
	private class TableFlux extends AbstractTableModel {
		private ArrayList<Object[]> flux;
		private String index[] =  {"Message","Date","Lieu","Utilisateur"};
		
		public TableFlux(ArrayList<Object[]> msgs) {
			super();
			flux = msgs;
		}
		
		
		public int getColumnCount() {
			return index.length;
		}

		public int getRowCount() {
			return flux.size();
		}
		
		
		public Object getValueAt(int ligne, int colonne) {
			MessageBDD msg = (MessageBDD) flux.get(ligne)[0];
			UserBDD user = (UserBDD) flux.get(ligne)[1];

			switch (colonne) {
				case 0:
					return msg.getContent();
				case 1:
					return msg.getDate();
				case 2:
					return msg.getLocalization();
				case 3:
					return user.getLogin() + " / " + user.getFirstName() + user.getName();
				default:
					return null;
			}
		}
		
		public String getColumnName(int colonne) {
	        return index[colonne];
	    }
		
		public void majTable(ArrayList<Object[]> fl) {
			flux = fl;
			fireTableDataChanged();
		}

		public void addToTable(Object[] msg) {
			flux.add(msg);
			fireTableDataChanged();
		}
	}

	public void addMessageToTableFlux(Object[] msg) {
		donneesTableFlux.addToTable(msg);
		revalidate();
	}

	public void addMessageToTableLoc(Object[] msg) {
		donneesTableLoc.addToTable(msg);
		revalidate();
	}

	public void razLoc() {
		textFieldLoc.setText("");
	}


}

	


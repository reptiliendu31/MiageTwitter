package user.ihm.panels;


import bdd.objetBdd.MessageBDD;
import user.ihm.UserIHM;
import user.ihm.enums.Etat;
import user.ihm.popup.PopupTweet;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.AbstractTableModel;

import javax.swing.JButton;

import java.awt.FlowLayout;

public class PanelMessages extends JPanel {
	private JPanel courant = this;
	private JPanel panelBox, panelCenter; 
	private BorderLayout layout ;
	private JTable tableauAbos ;
	private ArrayList<MessageBDD> messagesUsers;
	private JLabel lblListeMsg;
	private JPanel panelNorth;
	private UserIHM ihm;
	private TableMessages donneesTable;
	private JButton btnRetour;
	private JButton btnAjouterMsg;


	public PanelMessages(UserIHM i, ArrayList<MessageBDD> messages) {
		messagesUsers = messages;
		ihm = i;
		panelBox= new JPanel();
		panelCenter = new JPanel();
		layout = new BorderLayout();
		
		// Cr�ation du layout
		this.setLayout(layout);
				
		this.add(panelBox,BorderLayout.NORTH);
		lblListeMsg = new JLabel("Liste des messages :\r\n");
		lblListeMsg.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblListeMsg.setHorizontalAlignment(SwingConstants.LEFT);
		GroupLayout gl_panelBox = new GroupLayout(panelBox);
		gl_panelBox.setHorizontalGroup(
			gl_panelBox.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelBox.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblListeMsg)
					.addContainerGap(339, Short.MAX_VALUE))
		);
		gl_panelBox.setVerticalGroup(
			gl_panelBox.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelBox.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblListeMsg)
					.addContainerGap(19, Short.MAX_VALUE))
		);
		panelBox.setLayout(gl_panelBox);
				
		
		panelNorth = new JPanel();
		add(panelNorth, BorderLayout.SOUTH);
		panelNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnAjouterMsg = new JButton("Ajouter message");
		btnAjouterMsg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PopupTweet p = new PopupTweet(ihm);
			}
		});
		panelNorth.add(btnAjouterMsg);
		
		btnRetour = new JButton("Retour");
		btnRetour.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ihm.changerPanel(Etat.Dashboard);
			}
		});
		panelNorth.add(btnRetour);
		
		/* cr?ation du tableau  */
		panelCenter.setLayout(new GridLayout(0, 1, 20, 10));
		this.add(panelCenter,BorderLayout.CENTER);
		donneesTable = new TableMessages(messagesUsers);
		tableauAbos = new JTable(donneesTable);
		tableauAbos.setAutoCreateRowSorter(true);
		
		tableauAbos.setToolTipText("");
		ListSelectionModel listSelectionModel = tableauAbos.getSelectionModel();        
		JScrollPane scrollPane = new JScrollPane(tableauAbos);
		add(scrollPane, BorderLayout.CENTER);
		//listSelectionModel.addListSelectionListener(new ControleurTable());
	}
	
	
	// Mod�le pour la JTable
	private class TableMessages extends AbstractTableModel {
		private ArrayList<MessageBDD> messages ;
		private String index[] =  {"Message","Date d'envoi","Lieu"};
		
		public TableMessages(ArrayList<MessageBDD> lMess) {
			super();
			messages = lMess ;
		}
		
		public int getColumnCount() {
			return index.length;
		}

		public int getRowCount() {
			return messages.size();
		}
		
		public MessageBDD getMessage(int ligne) {
			return messages.get(ligne);
		}
		
		public Object getValueAt(int ligne, int colonne) {
			MessageBDD m = messages.get(ligne);
			switch (colonne) {
				case 0:
					return m.getContent();
				case 1:
					return m.getDate().toString();
				case 2:
					return m.getLocalization();
				default:
					return null;
			}
		}
		
		public String getColumnName(int colonne) {
	        return index[colonne];
	    }
		
		public void majTable(ArrayList<MessageBDD> msgs) {
			msgs = messages;
			fireTableDataChanged();
		}
	}
	
	public void rechargerTableau(ArrayList<MessageBDD> msgs) {
		donneesTable.majTable(msgs);
		revalidate();
	}
}

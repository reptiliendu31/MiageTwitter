package user.ihm.panels;

import bdd.objetBdd.MessageBDD;

import java.awt.BorderLayout;
import java.awt.Font;
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

import bdd.objetBdd.UserBDD;
import user.User;
import user.ihm.UserIHM;
import user.ihm.enums.Etat;

import javax.swing.JButton;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.BoxLayout;

public class PanelUser extends JPanel {
	private JPanel courant = this;
	private JPanel panelBox, panelCenter; 
	private BorderLayout layout ;
	private JTable tableauStations ;
	private ArrayList<MessageBDD> messagesFlux;
	private JLabel lblMurMessages ;
	private JPanel panelNorth;
	private UserIHM ihm;
	private TableFlux donneesTable;
	private final JButton btnChercher = new JButton("G�rer abonn�s");
	private JButton btnGrerMesMessages;
	private JLabel lblLocalisation;
	private JTextField textFieldLoc;
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
		
		textFieldLoc = new JTextField(user.getLocalisation());
		textFieldLoc.setColumns(10);
		
		JButton btnChanger = new JButton("Changer");
		GroupLayout gl_panelBox = new GroupLayout(panelBox);
		gl_panelBox.setHorizontalGroup(
			gl_panelBox.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelBox.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelBox.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblMurMessages)
						.addGroup(gl_panelBox.createSequentialGroup()
							.addComponent(lblIdentifiant)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(lblCodeSecret)))
					.addPreferredGap(ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
					.addComponent(lblLocalisation)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(textFieldLoc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnChanger)
					.addContainerGap())
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
						.addComponent(lblMurMessages)
						.addComponent(lblLocalisation)
						.addComponent(textFieldLoc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnChanger))
					.addContainerGap())
		);
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
				ihm.changerPanel(Etat.Menu);
			}
		});
		panelNorth.add(btnDconnexion);
		
		/* cr?ation du tableau  */
		donneesTable = new TableFlux(fl);
		
		add(panelCenter, BorderLayout.CENTER);
		panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.X_AXIS));
		tableauStations = new JTable(donneesTable);
		tableauStations.setAutoCreateRowSorter(true);
		
		tableauStations.setToolTipText("");
		ListSelectionModel listSelectionModel = tableauStations.getSelectionModel();
		JScrollPane scrollPane = new JScrollPane(tableauStations);
		panelCenter.add(scrollPane);
		
		
		JTable tableauStations2 = new JTable(donneesTable);
		tableauStations2.setAutoCreateRowSorter(true);
		
		tableauStations2.setToolTipText("");
		JScrollPane scrollPane2 = new JScrollPane(tableauStations);
		panelCenter.add(scrollPane2);

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

	public void addMessageToTable(Object[] msg) {
		donneesTable.addToTable(msg);
		revalidate();
	}

}

	


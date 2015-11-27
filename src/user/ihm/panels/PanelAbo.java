package user.ihm.panels;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import bdd.objetBdd.UserBDD;
import user.ihm.UserIHM;
import user.ihm.enums.Etat;

import javax.swing.JButton;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelAbo extends JPanel {
	private JPanel courant = this;
	private JPanel panelBox, panelCenter; 
	private BorderLayout layout ;
	private JTable tableauAbos ;
	private ArrayList<UserBDD> lAbonnes;
	private JLabel lblListeDesStations;
	private JPanel panelNorth;
	private UserIHM ihm;
	private TableAbonnes donneesTable;
	private JButton btnDesabo;
	private JButton btnRetour;
	private JButton btnAjouterAbonnement;
	private UserBDD abonneCourant;


	public PanelAbo(UserIHM i) {
		lAbonnes = new ArrayList<>() ;
		ihm = i;
		panelBox= new JPanel();
		panelCenter = new JPanel();
		layout = new BorderLayout();
		
		// Cr�ation du layout
		this.setLayout(layout);
				
		this.add(panelBox,BorderLayout.NORTH);
		lblListeDesStations = new JLabel("Liste des abonnements :\r\n");
		lblListeDesStations.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblListeDesStations.setHorizontalAlignment(SwingConstants.LEFT);
		GroupLayout gl_panelBox = new GroupLayout(panelBox);
		gl_panelBox.setHorizontalGroup(
			gl_panelBox.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelBox.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblListeDesStations)
					.addContainerGap(339, Short.MAX_VALUE))
		);
		gl_panelBox.setVerticalGroup(
			gl_panelBox.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelBox.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblListeDesStations)
					.addContainerGap(19, Short.MAX_VALUE))
		);
		panelBox.setLayout(gl_panelBox);
				
		
		panelNorth = new JPanel();
		add(panelNorth, BorderLayout.SOUTH);
		panelNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnDesabo = new JButton("Se d�sinscrire");
		btnDesabo.setEnabled(false);
		btnDesabo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		btnAjouterAbonnement = new JButton("Ajouter abonnement");
		panelNorth.add(btnAjouterAbonnement);
		panelNorth.add(btnDesabo);
		
		btnRetour = new JButton("Retour");
		btnRetour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ihm.changerPanel(Etat.Dashboard);
			}
		});

		panelNorth.add(btnRetour);
		
		/* cr?ation du tableau  */
		panelCenter.setLayout(new GridLayout(0, 1, 20, 10));
		this.add(panelCenter,BorderLayout.CENTER);
		donneesTable = new TableAbonnes(lAbonnes);
		tableauAbos = new JTable(donneesTable);
		tableauAbos.setAutoCreateRowSorter(true);
		
		tableauAbos.setToolTipText("");
		ListSelectionModel listSelectionModel = tableauAbos.getSelectionModel();        
		JScrollPane scrollPane = new JScrollPane(tableauAbos);
		add(scrollPane, BorderLayout.CENTER);
		listSelectionModel.addListSelectionListener(new ControleurTable());
	}
	
	
	// Mod�le pour la JTable
	private class TableAbonnes extends AbstractTableModel {

		private ArrayList<UserBDD> abos ;
		private String index[] =  {"Login abonn�","Nom"};
		
		public TableAbonnes(ArrayList<UserBDD> lAbos) {
			super();
			abos = lAbos ;
		}
		
		public int getColumnCount() {
			return index.length;
		}

		public int getRowCount() {
			return abos.size();
		}
		
		public UserBDD getAbonne(int ligne) {
			return abos.get(ligne);
		}
		
		public Object getValueAt(int ligne, int colonne) {
			UserBDD a = abos.get(ligne);
			switch (colonne) {
			case 0:
				return a.getLogin();
			case 1:
				return a.getFirstName() + " " + a.getName();
			default:
				return null;
			}
		}
		
		public String getColumnName(int colonne) {
	        return index[colonne];
	    }
		
		public void majTable(ArrayList<UserBDD> as) {
			abos = as;
			fireTableDataChanged();
		}
	}
	
	// Controleur qui r�cup�re la ligne s�lectionn�e
	private class ControleurTable  implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent listSelectionEvent) {
			if (listSelectionEvent.getValueIsAdjusting())
	            return;
	        ListSelectionModel lsm = (ListSelectionModel)listSelectionEvent.getSource();
	        if (!lsm.isSelectionEmpty()) {
	        	abonneCourant = lAbonnes.get(tableauAbos.convertRowIndexToModel(tableauAbos.getSelectedRow()));
	        	btnDesabo.setEnabled(true);
	        }			
		}
	}		
	
	public void rechargerTableau(ArrayList<UserBDD> nouveauxAbos) {
		donneesTable.majTable(nouveauxAbos);
		revalidate();
	}
}

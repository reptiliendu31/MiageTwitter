package user.ihm.popup;


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
import javax.swing.table.AbstractTableModel;

import bdd.objetBdd.UserBDD;
import user.ihm.UserIHM;

import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

public class PopupRechercheAbo extends JDialog {
	private JPanel panelBox, panelCenter; 
	private BorderLayout layout ;
	private JTable tableauAbos ;
	private ArrayList<UserBDD> lAbonnes;
	private JLabel lblListeDesStations;
	private JPanel panelNorth;
	private UserIHM ihm;
	private TableAbonnes donneesTable;
	private JButton btnRetour;
	private JButton btnAjouterAbonnement;
	private JButton btnRechercher;
	private JTextField textField;


	public PopupRechercheAbo(ArrayList<UserBDD> lA, UserIHM i) {
		setTitle("Ajouter des abonnements"); 
		setSize(450,400); 
		setLocationRelativeTo(null);
		setResizable(false);
		setModal (true);
		setAlwaysOnTop (true);
		setModalityType (ModalityType.APPLICATION_MODAL);

		lAbonnes = lA ;
		ihm = i;
		panelBox= new JPanel();
		panelCenter = new JPanel();
		layout = new BorderLayout();
		
		// Cr�ation du layout
		getContentPane().setLayout(layout);
				
		getContentPane().add(panelBox,BorderLayout.NORTH);
		lblListeDesStations = new JLabel("Liste des abonnements :\r\n");
		lblListeDesStations.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblListeDesStations.setHorizontalAlignment(SwingConstants.LEFT);
		
		btnRechercher = new JButton("Rechercher");
		
		textField = new JTextField();
		textField.setColumns(10);
		GroupLayout gl_panelBox = new GroupLayout(panelBox);
		gl_panelBox.setHorizontalGroup(
			gl_panelBox.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelBox.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblListeDesStations)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
					.addComponent(btnRechercher)
					.addContainerGap())
		);
		gl_panelBox.setVerticalGroup(
			gl_panelBox.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelBox.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelBox.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblListeDesStations)
						.addComponent(btnRechercher)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(19, Short.MAX_VALUE))
		);
		panelBox.setLayout(gl_panelBox);
				
		
		panelNorth = new JPanel();
		getContentPane().add(panelNorth, BorderLayout.SOUTH);
		panelNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnAjouterAbonnement = new JButton("Ajouter abonnement");
		panelNorth.add(btnAjouterAbonnement);
		
		btnRetour = new JButton("Retour");
		panelNorth.add(btnRetour);
		
		/* cr?ation du tableau  */
		panelCenter.setLayout(new GridLayout(0, 1, 20, 10));
		getContentPane().add(panelCenter,BorderLayout.CENTER);
		donneesTable = new TableAbonnes();
		tableauAbos = new JTable(donneesTable);
		tableauAbos.setAutoCreateRowSorter(true);
		
		tableauAbos.setToolTipText("");
		ListSelectionModel listSelectionModel = tableauAbos.getSelectionModel();        
		JScrollPane scrollPane = new JScrollPane(tableauAbos);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		//listSelectionModel.addListSelectionListener(new ControleurTable());
	}
	
	
	// Mod�le pour la JTable
	private class TableAbonnes extends AbstractTableModel {

		public int getColumnCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getRowCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		public Object getValueAt(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return null;
		}
		/*private ArrayList<Abonne> abos ;
		private String index[] =  {"Id Abonne","Code secret","Velo en location","D�but abonnement","Fin abonnement","Technicien"};
		
		public TableAbonnes(ArrayList<Abonne> lStations) {
			super();
			abos = lStations ;
		}
		
		public int getColumnCount() {
			return index.length;
		}

		public int getRowCount() {
			return abos.size();
		}
		
		public Abonne getAbonne(int ligne) {
			return abos.get(ligne);
		}
		
		public Object getValueAt(int ligne, int colonne) {
			Abonne a = abos.get(ligne);
			switch (colonne) {
			case 0:
				return a.getId();
			case 1:
				return a.getCode();
			case 2:
				return (a.hasVelo() ? a.getVeloCourant() : "Aucun");
			case 3:
				return a.getDateAboDebut();
			case 4:
				return a.getDateAboFin();
			case 5:
				return (a.isTechnicien() ? "Oui" : "Non");
			default:
				return null;
			}
		}
		
		public String getColumnName(int colonne) {
	        return index[colonne];
	    }
		
		public void majTable(ArrayList<Abonne> as) {
			abos = as;
			fireTableDataChanged();
		}*/
	}
	/*
	// Controleur qui r�cup�re la ligne s�lectionn�e
	private class ControleurTable  implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent listSelectionEvent) {
			if (listSelectionEvent.getValueIsAdjusting())
	            return;
	        ListSelectionModel lsm = (ListSelectionModel)listSelectionEvent.getSource();
	        if (!lsm.isSelectionEmpty()) {
	        	abonneCourant = lAbonnes.get(tableauAbos.convertRowIndexToModel(tableauAbos.getSelectedRow()));
	        	btnVoirAbonne.setEnabled(true);
	        }			
		}
	}		
	
	public void rechargerTableau(ArrayList<Abonne> nouveauxAbos) {
		donneesTable.majTable(nouveauxAbos);
	}*/
}

package user.ihm.popup;


import bdd.objetBdd.UserBDD;
import user.ihm.UserIHM;
import user.ihm.enums.Etat;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.LayoutStyle.ComponentPlacement;

public class PopupRechercheAbo extends JDialog {
	private JPanel panelBox, panelCenter;
	private BorderLayout layout ;
	private JTable tableauAbos ;
	private ArrayList<UserBDD> lAbonnes;
	private JLabel lblLAbo;
	private JPanel panelNorth;
	private UserIHM ihm;
	private TableAbonnes donneesTable;
	private JButton btnRetour;
	private JButton btnAjouterAbonnement;
	private UserBDD abonneCourant;
	private JTextField textFieldRecherche;


	public PopupRechercheAbo(UserIHM i) {
		setTitle("Rechercher abonnement");
		setSize(450,545);
		setLocationRelativeTo(null);
		setResizable(false);
		setModal (true);
		setAlwaysOnTop (true);
		setVisible(false);
		setModalityType (ModalityType.APPLICATION_MODAL);

		lAbonnes = new ArrayList<>() ;
		ihm = i;
		panelBox= new JPanel();
		panelCenter = new JPanel();
		layout = new BorderLayout();
		
		// Cr�ation du layout
		this.setLayout(layout);
				
		this.add(panelBox,BorderLayout.NORTH);
		lblLAbo = new JLabel("Rechercher utilisateur :");
		lblLAbo.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblLAbo.setHorizontalAlignment(SwingConstants.LEFT);
		
		textFieldRecherche = new JTextField();
		textFieldRecherche.setColumns(10);
		JButton btnRechercher = new JButton("Rechercher");
		btnRechercher.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						ihm.actionRecherche(textFieldRecherche.getText());
					}
				}
		);
		GroupLayout gl_panelBox = new GroupLayout(panelBox);
		gl_panelBox.setHorizontalGroup(
				gl_panelBox.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelBox.createSequentialGroup()
								.addContainerGap()
								.addComponent(lblLAbo)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(textFieldRecherche, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnRechercher)
								.addContainerGap(40, Short.MAX_VALUE))
		);
		gl_panelBox.setVerticalGroup(
				gl_panelBox.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelBox.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_panelBox.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblLAbo)
										.addComponent(textFieldRecherche, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(btnRechercher))
								.addContainerGap(19, Short.MAX_VALUE))
		);

		panelBox.setLayout(gl_panelBox);
				
		
		panelNorth = new JPanel();
		add(panelNorth, BorderLayout.SOUTH);
		panelNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnAjouterAbonnement = new JButton("Ajouter abonnement");
		btnAjouterAbonnement.setEnabled(false);
		btnAjouterAbonnement.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ihm.actionAbonnement(abonneCourant.getLogin());
			}
		});
		panelNorth.add(btnAjouterAbonnement);
		
		btnRetour = new JButton("Retour");
		btnRetour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
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
				btnAjouterAbonnement.setEnabled(true);
			}
		}
	}

	public void rechargerTableau(ArrayList<UserBDD> nouveauxAbos) {
		lAbonnes = nouveauxAbos;
		donneesTable.majTable(nouveauxAbos);
		revalidate();
	}

	public void razPopup() {
		btnAjouterAbonnement.setEnabled(false);
		rechargerTableau(new ArrayList<>());
	}
}

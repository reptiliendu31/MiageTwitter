package user.ihm.panels;

import user.ihm.UserIHM;
import user.ihm.enums.Etat;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import javax.swing.JTextField;

import java.awt.Color;

public class PanelLogin extends JPanel {
	private JTextField textFieldId;
	private JTextField textFieldMdp;
	private JLabel lblIdent,lblMotDePasse,lblIdentifiant,labelErreur;
	private JButton btnValider;
	protected UserIHM modele;

	/**
	 * Create the panel.
	 */
	public PanelLogin(UserIHM m) {	
		modele = m;
		setLayout(null);
		 lblIdent = new JLabel("S'identifier :");
		lblIdent.setBounds(10, 11, 169, 14);
		add(lblIdent);
		
		JButton btnRetour = new JButton("Retour");
		btnRetour.setBounds(105, 156, 80, 23);
		add(btnRetour);
		
		 lblIdentifiant = new JLabel("Identifiant :");
		lblIdentifiant.setBounds(30, 65, 131, 14);
		add(lblIdentifiant);
		
		 lblMotDePasse = new JLabel("Mot de passe :");
		lblMotDePasse.setBounds(30, 90, 131, 14);
		add(lblMotDePasse);
		
		textFieldId = new JTextField();
		textFieldId.setBounds(194, 62, 235, 20);
		add(textFieldId);
		textFieldId.setColumns(10);
		
		textFieldMdp = new JTextField();
		textFieldMdp.setBounds(194, 87, 235, 20);
		add(textFieldMdp);
		textFieldMdp.setColumns(10);
		
		btnValider = new JButton("Valider");
		btnValider.setBounds(10, 156, 85, 23);
		add(btnValider);
		
		labelErreur = new JLabel("");
		labelErreur.setForeground(Color.RED);
		labelErreur.setBounds(30, 36, 399, 14);
		add(labelErreur);
		
		btnRetour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modele.changerPanel(Etat.Menu);
			}
		});

		btnValider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modele.actionConnexion(textFieldId.getText(),textFieldMdp.getText());
			}
		});	
	}

	public void afficherErreurEssai() {
		labelErreur.setText("Erreur : identifiant ou mot de passe incorrect");
		razChampsSaisie();
	}
	
	public void remiseAZero(){
		labelErreur.setText("");
		btnValider.setEnabled(true);
		textFieldId.setEnabled(true);
		textFieldMdp.setEnabled(true);
		razChampsSaisie();
	}
	
	public void razChampsSaisie() {
		for (Component C : this.getComponents()){
			if(C instanceof JTextField){
				((JTextComponent) C ).setText("");
			}
		}
	}

}

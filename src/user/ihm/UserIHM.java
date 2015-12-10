package user.ihm;

import bdd.objetBdd.MessageBDD;
import bdd.objetBdd.UserBDD;
import user.User;
import user.ihm.enums.Etat;
import user.ihm.panels.*;
import user.ihm.popup.PopupErreur;
import user.ihm.popup.PopupRechercheAbo;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Timestamp;
import java.util.ArrayList;

public class UserIHM extends JFrame {
	private JPanel panelCourant;
	private JPanel contentPane;
	private PanelSubscribe panelSubscribe;
	private PanelLogin panelLogin;
	private PanelAccueil panelMenu;
	private PanelUser panelUser;
	private PanelAbo panelAbo;
	private PanelMessages panelMsg;
	private PopupRechercheAbo popupRechercheAbo;
	private User user;
	private UserBDD userCourant;
	
	public UserIHM(User u) {
		try {
			// Permet de prendre l'apparence du syst�me h�te
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e){}

		user = u;

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				user.signOut();
				user.closeConnections();
			}
		});
		System.out.println("IHM lancée");
		setTitle("TwittoMiage"); 
		setSize(470, 288);
		setVisible(false);
		setLocationRelativeTo(null); 
		setResizable(false); 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

		getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		contentPane = new JPanel();
		contentPane.setLayout(new GridLayout());
		panelSubscribe = new PanelSubscribe(this);
		panelMenu = new PanelAccueil(this);
		panelLogin = new PanelLogin(this);
		panelAbo = new PanelAbo(this);
		popupRechercheAbo = new PopupRechercheAbo(this);

		this.setContentPane(contentPane);
		panelCourant = panelMenu;
		changerPanel(Etat.Menu);
	}

	public void appellerPopupRecherche() {
		popupRechercheAbo.razPopup();
		popupRechercheAbo.setVisible(true);
	}

	public void actionRecherche(String recherche) {
		user.sendMsgSearch(recherche);
	}

	public void actionDesabo(String loginAbonnement) {
		user.sendMsgUnFollow(loginAbonnement);
	}


	public void actionAbonnement(String loginAbonnement) {
		user.sendMsgFollow(loginAbonnement);
	}

	public void actionInscription(String login, String pwd, String name, String firstName, String loc) {
		user.sendMsgSignIn(login,pwd,name,firstName,loc);
	}

	public void actionConnexion(String login, String pwd) {
		user.sendMsgConnection(login, pwd);
	}

	public void actionSendTweet(String text) {
		user.sendTweet(text);
	}

	public void actionSendLoc(String loc) {
		user.sendMsgLoc(loc);
	}

	public void callbackAbonnementSuccessful(ArrayList<UserBDD> abos) {
		panelAbo.rechargerTableau(abos);
		PopupErreur p = new PopupErreur("Abonnement","Abonnement réussi !");
		popupRechercheAbo.dispose();
	}

	public void callbackAbonnementFailed() {
		PopupErreur p = new PopupErreur("Abonnement","Abonnement échoué !");
		popupRechercheAbo.dispose();
	}

	public void callbackDesabonnementSuccessful(ArrayList<UserBDD> abonnes) {
		panelAbo.rechargerTableau(abonnes);
		PopupErreur p = new PopupErreur("Désabonnement","Désabonnement réussi !");
	}

	public void callbackDesabonnementFailed() {
		PopupErreur p = new PopupErreur("Désabonnement","Désabonnement échoué !");
	}

	public void callbackConnexionSuccessful() {
		userCourant = user.getUserCourant();

		// ajout des messages des abonnements
		ArrayList<Object[]> msgs = new ArrayList<>();
		for (UserBDD abonnement : userCourant.getAbonnements()
			 ) {
			for (MessageBDD msg : abonnement.getMessages()
				 ) {
				Object[] tabMessages = new Object[2];
				tabMessages[0] = msg;
				tabMessages[1] = abonnement;
				msgs.add(tabMessages);
			}
		}
		panelUser= new PanelUser(this,userCourant,msgs);
		panelMsg = new PanelMessages(this,userCourant.getMessages());

		panelAbo.rechargerTableau(userCourant.getAbonnements());
		panelMsg.rechargerTableau(userCourant.getMessages());

		this.changerPanel(Etat.Dashboard);
	}

	public void callbackConnexionFailed(String error) {
		PopupErreur p = new PopupErreur("Connexion","Connexion échouée : " + error);
	}

	public void callbackSubscribeFailed() {
		PopupErreur p = new PopupErreur("Inscription","Inscription échouée, veuillez réessayer.");
	}

	public void callbackSubscribeSuccessful() {
		PopupErreur p = new PopupErreur("Inscription","Inscription réussie, vous pouvez maintenant vous connecter.");
		this.changerPanel(Etat.Menu);
	}

	public void callbackLocSuccessfull(String locTemp) {
		PopupErreur p = new PopupErreur("Localisation","Localisation changée à " + locTemp);
	}

	public void callbackLocFailed() {
		panelUser.razLoc();
		PopupErreur p = new PopupErreur("Localisation","Erreur : localisation inchangée.");
	}

	public void callbackRechercheSuccessfull(ArrayList<UserBDD> recherche) {
		popupRechercheAbo.rechargerTableau(recherche);
	}

	public void callbackRechercheFailed() {
		PopupErreur p = new PopupErreur("Recherche","Erreur : recherche échouée.");
	}


	public void majTweetsUser() {
		panelMsg.rechargerTableau(userCourant.getMessages());
	}

	public void majFlux(Object[] o){
		panelUser.addMessageToTableFlux(o);
	}
	public void majLoc(Object[] o){
		panelUser.addMessageToTableLoc(o);
	}

	public void changerPanel(Etat e) {
		JPanel p;
		switch (e) {
		case Menu:
			setSize(470, 288);
			p = panelMenu;
			panelSubscribe.remiseAZero();
			panelLogin.remiseAZero();
			break;
		case Login:
			setSize(470, 288);
			p = panelLogin;
			break;
		case Subscribe:
			setSize(470, 288);
			p = panelSubscribe;
			break;		
		case Dashboard:
			p = panelUser;
			setSize(450,580);
			break;
		case Abonnes:
			setSize(470, 288);
			p = panelAbo;
			break;
		case Messages:
			setSize(470, 288);
			p = panelMsg;
			break;
		default:
			p = panelMenu;
			break;
		}
		panelCourant.setVisible(false);
		contentPane.removeAll();
		panelCourant = p;
		contentPane.add(panelCourant);
		panelCourant.setVisible(true);
		revalidate();
	}

	public void actionDisconnect() {
		user.signOut();
	}

}

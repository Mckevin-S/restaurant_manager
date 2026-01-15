import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

class WebSocketService {
    constructor() {
        this.stompClient = null;
        this.subscriptions = {};
    }

    connect(onConnected, onError) {
        const baseUrl = import.meta.env.VITE_API_URL ? import.meta.env.VITE_API_URL.replace('/api', '') : 'http://localhost:3006';
        const socket = new SockJS(`${baseUrl}/ws-restaurant`);
        this.stompClient = Stomp.over(socket);
        this.stompClient.debug = null; // Désactiver les logs console

        this.stompClient.connect({}, () => {
            console.log('Connecté au WebSocket');
            if (onConnected) onConnected();
        }, (error) => {
            console.error('Erreur WebSocket:', error);
            if (onError) onError(error);
        });
    }

    subscribe(topic, callback) {
        if (!this.stompClient || !this.stompClient.connected) {
            console.warn('Tentative d\'abonnement sans connexion WebSocket');
            return;
        }

        const subscription = this.stompClient.subscribe(topic, (message) => {
            try {
                const data = JSON.parse(message.body);
                callback(data);
            } catch (e) {
                console.error('Erreur de parsing message WebSocket:', e);
            }
        });

        this.subscriptions[topic] = subscription;
        return subscription;
    }

    unsubscribe(topic) {
        if (this.subscriptions[topic]) {
            this.subscriptions[topic].unsubscribe();
            delete this.subscriptions[topic];
        }
    }

    disconnect() {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.disconnect();
        }
    }

}

export default new WebSocketService();

import React from 'react';
import {
  StyleSheet,
  ScrollView,
  View,
  TouchableOpacity,
  Text,
  NativeModules,
  Image,
} from 'react-native';
import imgBase64 from './Base64';

function App() {
  const {SafraPay} = NativeModules;
  const {VENDA_DEBITO, VENDA_QRCODE, VENDA_CREDITO_PARC_COM_JUROS} = SafraPay;
  const [isActivate, setActivate] = React.useState(false as Boolean);

  function showMsg() {
    SafraPay.show('Classe Java' as String);
  }

  function handleActivate() {
    setActivate(true);
    SafraPay.activate(
      'Empresa' as String,
      'Automacao' as String,
      '1.0' as String,
    )
      .then((response: any) => {
        console.log('22', response);
      })
      .catch((error: any) => {
        console.log('25', error);
        setActivate(false);
      });
  }

  function handlePayDebit() {
    SafraPay.payment(
      (10 * 100) as Number,
      VENDA_DEBITO as Number,
      '123456789' as String,
    )
      .then((response: any) => {
        console.log('45', response);
      })
      .catch((error: any) => {
        console.log('48', error);
      });
  }

  function handlePayCredit() {
    //VENDA_CREDITO_A_VISTA
    //VENDA_CREDITO_PARC_COM_JUROS
    //VENDA_CREDITO_PARC_SEM_JUROS
    SafraPay.payment(
      (10 * 100) as Number,
      VENDA_CREDITO_PARC_COM_JUROS as Number,
      '123456789' as String,
    )
      .then((response: any) => {
        console.log('45', response);
      })
      .catch((error: any) => {
        console.log('48', error);
      });
  }

  function handlePayPix() {
    SafraPay.payment(
      (10 * 100) as Number,
      VENDA_QRCODE as Number,
      '123456789' as String,
    )
      .then((response: any) => {
        console.log('45', response);
      })
      .catch((error: any) => {
        console.log('48', error);
      });
  }
  function handleReversal() {
    SafraPay.reversal('123456789' as String)
      .then((response: any) => {
        console.log('85', response);
      })
      .catch((error: any) => {
        console.log('88', error);
      });
  }

  function handleReprint() {
    SafraPay.reprint('123456789' as String)
      .then((response: any) => {
        console.log('85', response);
      })
      .catch((error: any) => {
        console.log('88', error);
      });
  }

  function handlePrint() {
    SafraPay.print(imgBase64 as String)
      .then((response: any) => {
        console.log('85', response);
      })
      .catch((error: any) => {
        console.log('88', error);
      });
  }

  return (
    <ScrollView style={styles.scrollView}>
      <View style={styles.container}>
        <TouchableOpacity style={styles.button} onPress={() => showMsg()}>
          <Text>Testar Comunicação com Nativo</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={!isActivate ? styles.buttonRed : styles.buttonGreen}
          onPress={() => handleActivate()}>
          <Text>{!isActivate ? 'Ativar' : 'Pronto para Uso'}</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.button}
          onPress={() => handlePayDebit()}>
          <Text>Pagamento Debito</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.button}
          onPress={() => handlePayCredit()}>
          <Text>Pagamento Crédito</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={() => handlePayPix()}>
          <Text>Pagamento Pix</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.button}
          onPress={() => handleReversal()}>
          <Text>Reembolso</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={() => handleReprint()}>
          <Text>Reimpressão</Text>
        </TouchableOpacity>

        <Image
          style={styles.img}
          source={{
            uri: `data:image/jpg;base64,${imgBase64}`,
          }}
        />

        <TouchableOpacity style={styles.button} onPress={() => handlePrint()}>
          <Text>Imprimir Imagem</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  button: {
    alignItems: 'center',
    backgroundColor: '#DDDDDD',
    padding: 15,
  },
  buttonRed: {
    alignItems: 'center',
    backgroundColor: '#F44336',
    padding: 15,
  },
  buttonGreen: {
    alignItems: 'center',
    backgroundColor: '#00E676',
    padding: 15,
  },
  scrollView: {
    marginHorizontal: 20,
  },
  img: {
    width: 500,
    height: 500,
    resizeMode: 'contain',
  },
});

export default App;

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
  return (
    <ScrollView style={styles.scrollView}>
      <View style={styles.container}>
        <TouchableOpacity style={styles.button} onPress={() => {}}>
          <Text>Mostrar Mensagem</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={() => {}}>
          <Text>Pagamento</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={() => {}}>
          <Text>Reembolso</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={() => {}}>
          <Text>Reimpressão</Text>
        </TouchableOpacity>

        <Image
          style={styles.img}
          source={{
            uri: `data:image/jpg;base64,${imgBase64}`,
          }}
        />

        <TouchableOpacity style={styles.button} onPress={() => {}}>
          <Text>Gerar Imagem</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={() => {}}>
          <Text>Status Impressora</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.button} onPress={() => {}}>
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

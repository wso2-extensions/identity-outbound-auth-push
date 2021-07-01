import React, {useEffect} from 'react';
import {
  View,
  Image,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
} from 'react-native';
import AccountCard from '../components/AccountCard';
import AsyncStorage from '@react-native-async-storage/async-storage';

import {
  widthPercentageToDP as wp,
  heightPercentageToDP as hp,
} from 'react-native-responsive-screen';
import {useState} from 'react';

let trigger = true;

const AccountsScreen = ({route, navigation}) => {
  const [data, setData] = useState([]);
  // getAccounts();
  console.log('Fire!');

  route.params ? console.log('Green') : console.log('Black');

  console.log('Data in accounts screen: ' + data);

  /*
   * Loading the data when the accounts screen is focussed to be populated in the cards
   */
  useEffect(() => {
    const unsubscribe = navigation.addListener('focus', () => {
      const getData = async () => {
        await AsyncStorage.getItem('accounts').then((accounts) => {
          console.log(JSON.stringify(data) + ' and ' + accounts);
          if (accounts !== JSON.stringify(data)) {
            setData(JSON.parse(accounts));
            console.log('Changed so set');
          } else if (accounts === []) {
            setData([]);
          } else {
            console.log('Always running issue!');
          }
        });
      };
      getData();
    });
    return unsubscribe;
  }, [data, navigation]);

  const renderItem = ({item}) => <AccountCard account={item} />;

  return (
    <View
      style={{
        flexDirection: 'column',
        height: hp('97%'),
        justifyContent: 'center',
        marginTop: '3%',
        backgroundColor: '#FFF',
      }}>
      <View style={styles.logoView}>
        <Image
          source={require('../assets/img/wso2logo.png')}
          style={styles.logo}
        />
      </View>
      <View style={styles.titleView}>
        <Text style={styles.title}>Accounts</Text>
      </View>
      <View style={{flex: 13, flexDirection: 'column'}}>
        <FlatList
          data={data}
          renderItem={renderItem}
          extraData={trigger}
          keyExtractor={(item) => item.accountId.toString()}
          style={styles.accountsList}
        />
      </View>
      <TouchableOpacity
        activeOpacity={0.9}
        onPress={() => {
          console.log('Add button pressed');
          navigation.navigate('Add Account');
        }}
        style={styles.addButton}>
        <Image source={require('../assets/img/add-button.png')} />
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  buttonRoot: {
    marginTop: 64,
  },
  buttonView: {
    marginVertical: 10,
    marginHorizontal: '10%',
  },
  container: {
    marginVertical: '40%',
    alignContent: 'center',
    flexDirection: 'column',
    justifyContent: 'center',
  },
  logo: {
    alignSelf: 'center',
    width: '20%',
    resizeMode: 'contain',
  },
  logoText: {
    color: '#f47b20',
    textAlign: 'center',
    fontWeight: '600',
    fontSize: 18,
    top: -30,
    left: 20,
  },
  logoView: {
    flex: 3,
  },
  title: {
    fontSize: 36,
    // fontWeight: '300',
    fontFamily: 'Roboto-Light',
    textAlign: 'center',
  },
  titleView: {
    flex: 2,
  },
  button: {
    top: '-35%',
    paddingHorizontal: '10%',
  },
  addAccImage: {
    alignSelf: 'center',
  },
  addAccSteps: {
    alignSelf: 'center',
    width: '85%',
    resizeMode: 'contain',
    top: '-15%',
  },
  addButton: {
    position: 'absolute',
    bottom: hp('10%'),
    right: wp('5%'),
  },
  accountsList: {
    flexGrow: 1,
  },
});

export default AccountsScreen;

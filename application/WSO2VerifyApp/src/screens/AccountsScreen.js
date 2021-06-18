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
import BottomNavigation from '../components/BottomNavigation';
import {NavigationContainer} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';
import AsyncStorage from '@react-native-async-storage/async-storage';

import {
  widthPercentageToDP as wp,
  heightPercentageToDP as hp,
} from 'react-native-responsive-screen';
import {useState} from 'react';

// state = {data: []};
let trigger = true;
//   = [
//   {
//     accountId: 1,
//     username: 'kushanb@wso2.com',
//     displayName: 'Kushan Bhareti',
//     organization: 'WSO2',
//   },
//   {
//     accountId: 2,
//     username: 'blue@wso2.com',
//     displayName: 'Blue Hudsen',
//     organization: 'WSO2',
//   },
//   {
//     accountId: 3,
//     username: 'jonathan@wso2.com',
//     displayName: 'Jonathan Swiss',
//     organization: 'WSO2',
//   },
//   {
//     accountId: 4,
//     username: 'nike@google.com',
//     displayName: 'Nike Crane',
//     organization: 'Google',
//     device: 'Phone',
//   },
// ];

// const getAccounts = async () => {
//   try {
//     await AsyncStorage.getItem('accounts').then((accounts) => {
//       // console.log('Accounts loaded in Accounts screen: ' + accounts);
//       this.setState(JSON.parse(accounts));
//       // console.log('New data: ' + JSON.stringify(data));
//       // return JSON.parse(accounts);
//     });
//     // console.log('Test async value:' + value);
//     // console.log('data before: ' + data);
//     // value = JSON.parse(value);
//     // value.accountId = data.length + 1;
//     // data.push(value);
//     // console.log('data after: ' + data);
//     // console.log('Async accounts: ' + JSON.stringify(data[data.length]));
//   } catch (e) {
//     console.log('No accounts available');
//     await AsyncStorage.setItem('accounts', JSON.stringify(data)).then(() => {
//       console.log('Async storage data initialized');
//     });
//   }
// };
// data = getAccounts();

// componentDidMount = () => {
//   getAccounts();
// };
// componentDidMount();

const AccountsScreen = ({route, navigation}) => {
  const [data, setData] = useState([]);
  // getAccounts();
  console.log('Fire!');

  route.params ? console.log('Green') : console.log('Black');

  // data.length == 0 ? getAccounts() : (data = data);
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
  }, [navigation]);

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

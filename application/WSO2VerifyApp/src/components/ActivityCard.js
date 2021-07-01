import React from 'react';
import {View, StyleSheet, Text} from 'react-native';
import {
    heightPercentageToDP as hp,
} from 'react-native-responsive-screen';
import {compareDate} from '../utils/Date';

const ActivityCard = ({account}) => {
    return (
        <View style={styles.cardView}>
            <Text style={styles.textCardContent}>
                <Text style={styles.bold}>
                    Name{'\t\t\t'}:{'\t'}
                </Text>{' '}
                {account.displayName ? account.displayName : 'N/A'}
            </Text>
            <Text style={styles.textCardContent}>
                <Text style={styles.bold}>
                    Username{'\t\t'}:{'\t'}
                </Text>{' '}
                {account.username ? account.username : 'N/A'}
            </Text>
            <Text style={styles.textCardContent}>
                <Text style={styles.bold}>
                    Organization{'\t'}:{'\t'}
                </Text>{' '}
                {account.organization ? account.organization : 'N/A'}
            </Text>
            <Text style={styles.textCardContent}>
                <Text style={styles.bold}>
                    Application{'\t\t'}:{'\t'}
                </Text>{' '}
                {account.applicationName ? account.applicationName : 'N/A'}
            </Text>
            <Text style={styles.textCardContent}>
                <Text style={styles.bold}>
                    Device{'\t\t\t'}:{'\t'}
                </Text>{' '}
                {account.deviceName ? account.deviceName : 'N/A'}
            </Text>
            <Text style={styles.textCardContent}>
                <Text style={styles.bold}>
                    Time{'\t\t\t'}:{'\t'}
                </Text>{' '}
                {compareDate(
                    account.requestTime.day,
                    account.requestTime.month,
                    account.requestTime.year,
                    account.requestTime.date,
                ) + ` `}
                | {account.requestTime.hour + `:` + account.requestTime.minute}
            </Text>
        </View>
    );
};

const styles = StyleSheet.create({
    cardView: {
        // width: '90%',
        elevation: 4,
        shadowColor: 'black',
        shadowOffset: {width: 1, height: 2},
        shadowOpacity: 0.2,
        shadowRadius: 8,
        backgroundColor: '#FFF',
        marginHorizontal: '5%',
        paddingHorizontal: '4%',
        paddingVertical: '2%',
        marginVertical: '5%',
        borderRadius: 20,
    },
    textOrganization: {
        fontSize: 14,
        fontFamily: 'Roboto-Regular',
        marginVertical: 5,
        color: '#7C7C7C',
    },
    textCardContent: {
        fontSize: hp('1.8%'),
        fontFamily: 'Roboto-Regular',
        marginVertical: 5,
    },
    bold: {
        fontFamily: 'Roboto-Medium',
    },
    deleteButton: {
        alignSelf: 'flex-end',
    },
});

export default ActivityCard;

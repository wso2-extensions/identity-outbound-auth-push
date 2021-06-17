let now = new Date();
console.log('Date taken');

export const compareDate = (day, month, year, date) => {
  if (year == now.getFullYear() && month == now.getMonth()) {
    if (day == now.getDate()) {
      return 'Today';
    } else if (now.date - day == 1) {
      return 'Yesterday';
    } else {
      return date;
    }
  }
};

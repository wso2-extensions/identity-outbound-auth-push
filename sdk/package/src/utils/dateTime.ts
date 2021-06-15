import { DateTimeInterface } from "src/models/dateTime";

export class DateTime {
  //   day: number;
  //   month: number;
  //   year: number;
  //   date: string;
  //   hour: number;
  //   minute: number;
  //   seconds: number;
  //     time: string;
  private dateTime: DateTimeInterface;

  public constructor() {
    let current = new Date();
    // this.day = current.getDate();
    // this.month = current.getMonth();
    // this.year = current.getFullYear();
    // this.hour = current.getHours();
    // this.minute = current.getMinutes();
    // this.seconds = current.getSeconds();
    this.dateTime = {
      day: current.getDate(),
      month: current.getMonth(),
      year: current.getFullYear(),
      hour: current.getHours(),
      minute: current.getMinutes(),
      seconds: current.getSeconds(),
      date:
        current.getDate() +
        "-" +
        current.getMonth() +
        "-" +
        current.getFullYear(),
      time:
        (current.getHours() < 12
          ? current.getHours()
          : current.getHours() - 12) +
        ":" +
        current.getMinutes() +
        (current.getHours() < 12)
          ? " a.m."
          : " p.m.",
    };

    // Structured formats
  }

  public getDateTime(): DateTimeInterface {
    return this.dateTime;
  }
}

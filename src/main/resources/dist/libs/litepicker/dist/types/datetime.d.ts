export declare class DateTime {
    static parseDateTime(date: Date | DateTime | string | number, format?: string, lang?: string): Date;

    static convertArray(array: Array<Date | Date[] | string | string[]>, format: string): Array<DateTime | DateTime[]>;

    static getDateZeroTime(date: Date): Date;

    private static regex;
    private static readonly MONTH_JS;
    private static shortMonths;
    private static longMonths;
    private static formatPatterns;
    protected lang: string;
    private dateInstance;

    constructor(date?: Date | DateTime | number | string, format?: object | string, lang?: string);

    toJSDate(): Date;

    toLocaleString(arg0: string, arg1: Intl.DateTimeFormatOptions): string;

    toDateString(): string;

    getSeconds(): number;

    getDay(): number;

    getTime(): number;

    getDate(): number;

    getMonth(): number;

    getFullYear(): number;

    setMonth(arg: number): number;

    setHours(hours?: number, minutes?: number, seconds?: number, ms?: number): void;

    setSeconds(arg: number): number;

    setDate(arg: number): number;

    setFullYear(arg: number): number;

    getWeek(firstDay: number): number;

    clone(): DateTime;

    isBetween(date1: DateTime, date2: DateTime, inclusivity?: string): boolean;

    isBefore(date: DateTime, unit?: string): boolean;

    isSameOrBefore(date: DateTime, unit?: string): boolean;

    isAfter(date: DateTime, unit?: string): boolean;

    isSameOrAfter(date: DateTime, unit?: string): boolean;

    isSame(date: DateTime, unit?: string): boolean;

    add(duration: number, unit?: string): DateTime;

    subtract(duration: number, unit?: string): DateTime;

    diff(date: DateTime, unit?: string): number;

    format(format: object | string, lang?: string): string;

    private timestamp;
    private formatTokens;
}
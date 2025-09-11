import type User from "./User";

export default interface AppData {
    backendUrl: string | null;
    token: string | null;
    user: User | null;
}
import { LoggerService } from "./logger";
import { ConfigService } from "./config";
import axios, { AxiosRequestConfig } from "axios";
import { AuthService } from "./auth";
import { ContentTypes } from "../models";

const AXIOS = axios.create();

/**
 * Interface implemented by all services.
 */
export interface Service {
    init(): void;
}

/**
 * Base class for all services.
 */
export abstract class BaseService implements Service {

    protected logger: LoggerService | undefined;
    protected config: ConfigService | undefined;
    protected auth: AuthService | undefined;

    public init(): void {
        this.initAuthInterceptor();
    }

    public initAuthInterceptor() {
        AXIOS.interceptors.request.use(this.auth?.getAuthInterceptor());
    }

    /**
     * Creates an endpoint to use when making a REST call.  Supports path params and query params.
     * @param path
     * @param params
     * @param queryParams
     */
    protected endpoint(path: string, params?: any, queryParams?: any): string {
        if (params) {
            Object.keys(params).forEach(key => {
                const value: string = encodeURIComponent(params[key]);
                path = path.replace(":" + key, value);
            });
        }
        let rval: string = this.apiBaseHref() + path;
        if (queryParams) {
            let first: boolean = true;
            for (const key in queryParams) {
                if (queryParams[key]) {
                    const value: string = encodeURIComponent(queryParams[key]);
                    if (first) {
                        rval = rval + "?" + key;
                    } else {
                        rval = rval + "&" + key;
                    }
                    if (value !== null && value !== undefined) {
                        rval = rval + "=" + value;
                    }
                    first = false;
                }
            }
        }
        this.logger?.info("[BaseService] Using REST endpoint: ", rval);
        return rval;
    }

    /**
     * Creates the request options used by the HTTP service when making API calls.
     * @param headers
     */
    protected options(headers: {[header: string]: string}): AxiosRequestConfig {
        const options: AxiosRequestConfig = { headers };
        return options;
    }

    /**
     * Performs an HTTP GET operation to the given URL with the given options.  Returns
     * a Promise to the HTTP response data.
     */
    protected httpGet<T>(url: string, options?: AxiosRequestConfig, successCallback?: (value: any) => T): Promise<T> {
        this.logger?.info("[BaseService] Making a GET request to: ", url);

        if (!options) {
            options = this.options({ "Accept": ContentTypes.APPLICATION_JSON });
        }

        const config: AxiosRequestConfig = this.axiosConfig("get", url, options);
        return AXIOS.request(config)
            .then(response => {
                const data: T = response.data;
                if (successCallback) {
                    return successCallback(data);
                } else {
                    return data;
                }
            }).catch(error => {
                return Promise.reject(this.unwrapErrorData(error));
            });
    }

    /**
     * Performs an HTTP POST operation to the given URL with the given body and options.  Returns
     * a Promise to null (no response data expected).
     * @param url
     * @param body
     * @param options
     * @param successCallback
     * @param progressCallback
     */
    protected httpPost<I>(url: string, body: I, options?: AxiosRequestConfig, successCallback?: () => void,
        progressCallback?: (progressEvent: any) => void): Promise<void>
    {
        this.logger?.info("[BaseService] Making a POST request to: ", url);

        if (!options) {
            options = this.options({ "Content-Type": ContentTypes.APPLICATION_JSON });
        }

        const config: AxiosRequestConfig = this.axiosConfig("post", url, options, body);
        if (progressCallback) {
            const fiftyMB: number = 50 * 1024 * 1024;
            config.onUploadProgress = progressCallback;
            config.maxContentLength = fiftyMB;
            config.maxBodyLength = fiftyMB;
        }
        return AXIOS.request(config)
            .then(() => {
                if (successCallback) {
                    return successCallback();
                } else {
                    return;
                }
            }).catch(error => {
                return Promise.reject(this.unwrapErrorData(error));
            });
    }

    /**
     * Performs an HTTP POST operation to the given URL with the given body and options.  Returns
     * a Promise to the HTTP response data.
     * @param url
     * @param body
     * @param options
     */
    protected httpPostWithReturn<I, O>(url: string, body: I, options?: AxiosRequestConfig, successCallback?: (data: any) => O): Promise<O> {
        this.logger?.info("[BaseService] Making a POST request to: ", url);

        if (!options) {
            options = this.options({ "Accept": ContentTypes.APPLICATION_JSON, "Content-Type": ContentTypes.APPLICATION_JSON });
        }

        const config: AxiosRequestConfig = this.axiosConfig("post", url, options, body);
        return AXIOS.request(config)
            .then(response => {
                const data: O = response.data;
                if (successCallback) {
                    return successCallback(data);
                } else {
                    return data;
                }
            }).catch(error => {
                return Promise.reject(this.unwrapErrorData(error));
            });
    }

    /**
     * Performs an HTTP PUT operation to the given URL with the given body and options.  Returns
     * a Promise to null (no response data expected).
     * @param url
     * @param body
     * @param options
     */
    protected httpPut<I>(url: string, body: I, options?: AxiosRequestConfig, successCallback?: () => void): Promise<void> {
        this.logger?.info("[BaseService] Making a PUT request to: ", url);

        if (!options) {
            options = this.options({ "Content-Type": ContentTypes.APPLICATION_JSON });
        }

        const config: AxiosRequestConfig = this.axiosConfig("put", url, options, body);
        return AXIOS.request(config)
            .then(() => {
                if (successCallback) {
                    return successCallback();
                } else {
                    return;
                }
            }).catch(error => {
                return Promise.reject(this.unwrapErrorData(error));
            });
    }

    /**
     * Performs an HTTP PUT operation to the given URL with the given body and options.  Returns
     * a Promise to the HTTP response data.
     * @param url
     * @param body
     * @param options
     */
    protected httpPutWithReturn<I, O>(url: string, body: I, options?: AxiosRequestConfig, successCallback?: (data: O) => O): Promise<O> {
        this.logger?.info("[BaseService] Making a PUT request to: ", url);

        if (!options) {
            options = this.options({ "Accept": ContentTypes.APPLICATION_JSON, "Content-Type": ContentTypes.APPLICATION_JSON });
        }

        const config: AxiosRequestConfig = this.axiosConfig("put", url, options, body);
        return AXIOS.request(config)
            .then(response => {
                const data: O = response.data;
                if (successCallback) {
                    return successCallback(data);
                } else {
                    return data;
                }
            }).catch(error => {
                return Promise.reject(this.unwrapErrorData(error));
            });
    }

    /**
     * Performs an HTTP DELETE operation to the given URL with the given body and options.
     * @param url
     * @param options
     */
    protected httpDelete<T>(url: string, options?: AxiosRequestConfig, successCallback?: () => T): Promise<T> {
        this.logger?.info("[BaseService] Making a DELETE request to: ", url);

        if (!options) {
            options = {};
        }

        const config: AxiosRequestConfig = this.axiosConfig("delete", url, options);
        return AXIOS.request(config)
            .then(() => {
                return (successCallback ? successCallback() : null) as T;
            }).catch(error => {
                return Promise.reject(this.unwrapErrorData(error));
            });
    }

    protected apiBaseHref(): string {
        let artifactsUrl: string = this.config?.artifactsUrl() || "";
        if (artifactsUrl.endsWith("/")) {
            artifactsUrl = artifactsUrl.substring(0, artifactsUrl.length - 1);
        }
        this.logger?.debug("[BaseService] Base HREF of REST API: ", artifactsUrl);
        return artifactsUrl;
    }

    private axiosConfig(method: string, url: string, options: any, data?: any): AxiosRequestConfig {
        if (typeof data === "string") {
            data = new Blob([data]);
        }
        const c: any = {
            data,
            method,
            url,
            validateStatus: (status: number) => {
                return status >= 200 && status < 300;
            }
        };
        return { ...c, ...options };
    }

    private unwrapErrorData(error: any): any {
        if (error && error.response && error.response.data) {
            return {
                message: error.message,
                ...error.response.data,
                status: error.response.status
            };
        } else if (error && error.response) {
            return {
                message: error.message,
                status: error.response.status
            };
        } else if (error) {
            console.error("Unknown error detected: ", error);
            return {
                message: error.message,
                status: 500
            };
        } else {
            console.error("Unknown error detected: ", error);
            return {
                message: "Unknown error",
                status: 500
            };
        }
    }
}

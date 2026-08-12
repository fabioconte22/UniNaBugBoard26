import { Component, inject } from "@angular/core";
import { RouterLink } from "@angular/router";
import { AuthService } from "../../core/auth/auth.service";

@Component({
    selector: 'app-landing',
    imports: [RouterLink],
    templateUrl: './landing.html',
})
export class Landing {
    protected readonly auth = inject(AuthService); 
}
import { Component, inject } from "@angular/core";
import { RouterLink } from "@angular/router";
import { AuthService } from "../../core/auth/auth.service";
import { ThemeToggle } from "../../core/theme/theme-toggle/theme-toggle";

@Component({
    selector: 'app-landing',
    imports: [RouterLink, ThemeToggle],
    templateUrl: './landing.html',
})
export class Landing {
    protected readonly auth = inject(AuthService); 
}